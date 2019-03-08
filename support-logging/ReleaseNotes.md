# v0.2 (10/20/2017)
# Release Notes

## Notable Changes
The Barcelona Release (v 0.2) of the Support Logging micro service includes the following:
* Application of Google Style Guidelines to the code base
* Increase in unit/intergration tests from 10 tests to 182 tests
* POM changes for appropriate repository information for distribution/repos management, checkstyle plugins, etc.
* Added Dockerfile for creation of micro service targeted for ARM64 
* Added interfaces for all Controller classes
* Added implementation for log updates, deletes

## Bug Fixes
* Fix many bugs associated to logging queries
* Fixed issue that allows log file path to be platform agnostic

## Pull Request/Commit Details
 - [#14](https://github.com/edgexfoundry/support-logging/pull/14) - Remove staging plugin contributed by Jeremy Phelps ([JPWKU](https://github.com/JPWKU))
 - [#13](https://github.com/edgexfoundry/support-logging/pull/13) - Fixes Maven artifact dependency path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#12](https://github.com/edgexfoundry/support-logging/pull/12) - added staging and snapshots repos to pom along with nexus staging maven plugin contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#11](https://github.com/edgexfoundry/support-logging/pull/11) - Adding aarch64 dockerfile contributed by ([feclare](https://github.com/feclare))
 - [#10](https://github.com/edgexfoundry/support-logging/pull/10) - Adds Docker build capability contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#9](https://github.com/edgexfoundry/support-logging/pull/9) - added nexus repos elements to pom contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#8](https://github.com/edgexfoundry/support-logging/pull/8) - added checkstyle to pom, clean up of some checkstyle warnings, test s… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#7](https://github.com/edgexfoundry/support-logging/pull/7) - fixed many bugs as it relates to client query client calls and added … contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#6](https://github.com/edgexfoundry/support-logging/pull/6) - checkstyle changes, addition of unit tests, clean up and few bug fixes contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#5](https://github.com/edgexfoundry/support-logging/pull/5) - Fixes Log File Path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#4](https://github.com/edgexfoundry/support-logging/issues/4) - Log File Path not Platform agnostic
 - [#3](https://github.com/edgexfoundry/support-logging/pull/3) - Fixes Logging OOM Configuration contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#2](https://github.com/edgexfoundry/support-logging/pull/2) - Add distributionManagement for artifact stroage contributed by Andrew Grimberg ([tykeal](https://github.com/tykeal))
 - [#1](https://github.com/edgexfoundry/support-logging/pull/1) - Contributed Project Fuse source code contributed by Tyler Cox ([trcox](https://github.com/trcox))

