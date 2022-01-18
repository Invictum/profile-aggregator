Aggregator
-----

### Stack

Used stack:

- Java JDK 11
- SpringBoot
- Spring MVC

### Schema
<img src="schema.png" alt="drawing" width="500" height="500"/>

Explanation:

- `AggregatorApplication` Spring boot application entry point. Ignites Spring stack and runs application.
- `ProfileController` rest endpoint that serves routes at `GET <HOST>/proofiles/<ORG_NAME>`, where `<ORG_NAME>` is an
  organisation name to build profile for. Default host value is `http://localhos:8080`.
- `ProfileService` connection entity for `ProfileController`, `MergeStrategy` and `RepositoriesDataSource`. Collects
  repo info from all the registered sources and builds representation by applying concrete `MergeStrategy`.
- `RepositoriesDataSource` abstraction that allows to pull repos data from different sources and store them in unified
  way in `RepositoryData` objects. All the implementations of `RepositoriesDataSource` are automatically considered
  during data collection stage. Two implementations are available by default: `BitBucketSource` and `GitHubSource`.
- `MergeStrategy` provides abstraction that combines and represents collected `RepositoryData` in a specific
  way. `CombinedProfile` implementation builds composed statistics for the team using attached repo data sources.
- `Configuration`provides http client setup that used across `GitHubSource` and `BitBucketSource`

### Points to improve

Codebase:
 - Considering most repo sources has rate limits it makes sense to implement limiters to avoid over polling.
 - Profiles aggregation is I/O bound task, so changing the implementation to async variant (spring web-flux) will increase performance. 
 - Data can be retrieved in more effective way using GraphQL instead of REST api. Unfortunately bitbucket has poor documentation about it, so REST was used.
 - Increase unit tests coverage. Only positive cases are covered as for now.

Deployment to live:
 - Consider spawning several aggregator nodes with elastic load balancer for automatic scaling. 
 - Profile aggregation is relatively heavy operation and underlying data nature is relatively static. Introducing cache before API endpoint will dramatically reduce load. 
 - Add centralized logging solution, like Elasticsearch, Logstash, Kibana stack.

Future considerations:
 - Requested team names as well as other data discovered during aggregation might be a good foundation for data lake. So those data can be used to discover different trends like languages popularity over time.
