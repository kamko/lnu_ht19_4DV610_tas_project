# Changes to project
General changes to make the project more maintainable in the future

## Done

1. removed all files from the project which can be generated (compiled classes, docs, etc.)
2. removed eclipse project structure
3. added maven project structure
4. fixed resources paths
5. maven shade plugin (easily build fat jar)
6. add maven wrapper
7. add slf4j and logback

## Todo
- add explicit javafx dependencies (for openjdk11)
- add logging
- create readme

## bugfixes
1. 
   - Problem: In the developer guide is stated that `SimpleAdaptationEngine` will re-register all services.
That is true - it tries to do that but is not successful as this situation never happens.
Current implementation of `CompositeService` is reloading services if nothing was found
(that's because a cache is used there and to be honest everything goes through the cache which is not really a good way).
As a result of this `notifyServiceNotFound` is never called.
   - Solution: I implemented simple mechanism which will register service if it's first call for it. Further calls are directly looking into cache.
By doing this if no service is found (if all services were removed by adaptation engine) `notifyServiceNotFound` is correctly called and adaptation can be made.
**Warning:** this could mess the GUI selection of services a bit.
