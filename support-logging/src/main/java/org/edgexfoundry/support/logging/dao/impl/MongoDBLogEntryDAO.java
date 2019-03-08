/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-logging
 * @author: Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.logging.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.dao.MDC_ENUM_CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.SerializationUtils;
import org.springframework.stereotype.Component;

@Component("serviceDAO")
@ConditionalOnProperty(name = {"logging.persistence"}, havingValue = "mongodb")
public class MongoDBLogEntryDAO extends BaseLogEntryDAO {

  private static final Logger logger = LoggerFactory.getLogger(MongoDBLogEntryDAO.class);
  
  public MongoDBLogEntryDAO() {
    logger.info("Logging is using Mongo to persist log messages.");
  }

  @Autowired
  private MongoTemplate mongoTemplate;

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#save(org.edgexfoundry.support.
   * logging.domain.LogEntry)
   */
  @Override
  public boolean save(LogEntry entry) {
    boolean result = super.save(entry);
    if (result) {// only save the logEntry into MongoDB when it's loggable
      mongoTemplate.insert(entry);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#findByCriteria(org.
   * edgexfoundry.support.domain.logging.MatchCriteria, int)
   */
  @Override
  public List<LogEntry> findByCriteria(MatchCriteria criteria, int limit) {
    Query query = new Query();
    query.limit(limit);
    Criteria mongoCriteria = toCriteria(criteria);
    if (null != mongoCriteria) {
      query.addCriteria(mongoCriteria);
    }
    return mongoTemplate.find(query, LogEntry.class);
  }

  /**
   * Convert MatchCriteria into a MongoDB Query Criteria Ideally, a MatchCriteria could be converted
   * to a criteria that represents a mongodb query similar to following:
   * 
   * db.logEntry.find( { "$and" : [ { "created" : { "$gte" : 1476952483377}} , { "created" : {
   * "$lte" : 1477468656189}} ] , "logLevel" : { "$in" : [ "WARN" , "INFO"]} , "originService" : {
   * "$in" : [ "testService" , "service1"]} , "labels" : { "$in" : [ "15" , "l8", "l6"]} , "$or" : [
   * { "message" : { "$regex" : ".*edgex.*"}} , { "message" : { "$regex" : ".*edgexfoundry.*"}} ] }
   * )
   * 
   * @param criteria
   * @return MongoDB Query Criteria
   */
  private Criteria toCriteria(MatchCriteria criteria) {

    Criteria result = null;

    // only create Criteria when incoming MatchCriteria is not null and one
    // of start and end must be larger than zero
    if (null != criteria && (criteria.getStart() > 0 || criteria.getEnd() > 0)) {
      result = new Criteria().andOperator(
          Criteria.where(MDC_ENUM_CONSTANTS.CREATED.getValue()).gt(criteria.getStart()),
          Criteria.where(MDC_ENUM_CONSTANTS.CREATED.getValue()).lt(criteria.getEnd()));
      Level[] targetLevels = criteria.getLogLevels();
      if (null != targetLevels && targetLevels.length > 0) {
        // add criteria where logLevel must match to at least one
        // elements of targetLevels
        result = result.and(MDC_ENUM_CONSTANTS.LOGLEVEL.getValue()).in((Object[]) targetLevels);
      }
      result = addStringArrayCriteria(result, criteria.getOriginServices(), MDC_ENUM_CONSTANTS.ORIGINSERVICE.getValue());
      result = addStringArrayCriteria(result, criteria.getLabels(), MDC_ENUM_CONSTANTS.LABELS.getValue());
      String[] targetKeywords = criteria.getMessageKeywords();
      if (null != targetKeywords && targetKeywords.length > 0) {
        // add criteria where message must contain at least one elements
        // of targetKeywords
        List<Criteria> keywordList = new ArrayList<>();
        for (String keyword : targetKeywords) {
          keywordList.add(new Criteria(MDC_ENUM_CONSTANTS.MESSAGE.getValue())
              .regex(Pattern.compile(".*" + keyword + ".*")));
        }
        result = result.orOperator(keywordList.toArray(new Criteria[0]));
      }
      logger.info("mongoDB query criteria:{}",
          SerializationUtils.serializeToJsonSafely(result.getCriteriaObject()));
    }
    return result;
  }
  
  private Criteria addStringArrayCriteria(Criteria result, String[] targets, String key) {
    if (null != targets && targets.length > 0) {
      return result.and(key).in((Object[]) targets);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#removeByCriteria(org.
   * edgexfoundry.support.domain.logging.MatchCriteria)
   */
  @Override
  public List<LogEntry> removeByCriteria(MatchCriteria criteria) {
    Query query = new Query();
    Criteria mongoCriteria = toCriteria(criteria);
    if (null != mongoCriteria) {
      query.addCriteria(mongoCriteria);
    }
    return mongoTemplate.findAllAndRemove(query, LogEntry.class);
  }

}
