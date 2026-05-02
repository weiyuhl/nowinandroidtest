# `:benchmarks`

## Module Dependency Graph
<!--region graph-->
```mermaid
---
config:
  layout: elk
  elk:
    nodePlacementStrategy: SIMPLE
---
graph TB
  subgraph :route
    direction TB
    subgraph :route:test1
      direction TB
      :route:test1:contract[contract]:::android-route
      :route:test1:scene[scene]:::android-route
    end
    subgraph :route:test2
      direction TB
      :route:test2:contract[contract]:::android-route
      :route:test2:scene[scene]:::android-route
    end
    subgraph :route:test3
      direction TB
      :route:test3:contract[contract]:::android-route
      :route:test3:scene[scene]:::android-route
    end
    subgraph :route:search
      direction TB
      :route:search:contract[contract]:::android-route
      :route:search:scene[scene]:::android-route
    end
    subgraph :route:settings
      direction TB
      :route:settings:contract[contract]:::android-route
      :route:settings:scene[scene]:::android-route
    end
  end
  subgraph :core
    direction TB
    :core:analytics[analytics]:::android-library
    :core:common[common]:::jvm-library
    :core:data[data]:::android-library
    :core:database[database]:::android-library
    :core:datastore[datastore]:::android-library
    :core:datastore-proto[datastore-proto]:::jvm-library
    :core:designsystem[designsystem]:::android-library
    :core:model[model]:::jvm-library
    :core:navigation[navigation]:::android-library
    :core:network[network]:::android-library
    :core:ui[ui]:::android-library
  end
  :benchmarks[benchmarks]:::android-test
  :app[app]:::android-application

  :app -.->|baselineProfile| :benchmarks
  :app -.-> :core:analytics
  :app -.-> :core:common
  :app -.-> :core:data
  :app -.-> :core:designsystem
  :app -.-> :core:model
  :app -.-> :core:ui
  :app -.-> :route:search:contract
  :app -.-> :route:search:scene
  :app -.-> :route:settings:contract
  :app -.-> :route:settings:scene
  :app -.-> :route:test1:contract
  :app -.-> :route:test1:scene
  :app -.-> :route:test2:contract
  :app -.-> :route:test2:scene
  :app -.-> :route:test3:contract
  :app -.-> :route:test3:scene
  :benchmarks -.->|testedApks| :app
  :core:data -.-> :core:analytics
  :core:data --> :core:common
  :core:data --> :core:database
  :core:data --> :core:datastore
  :core:data --> :core:network
  :core:database --> :core:model
  :core:datastore -.-> :core:common
  :core:datastore --> :core:datastore-proto
  :core:datastore --> :core:model
  :core:network --> :core:common
  :core:network --> :core:model
  :core:ui --> :core:analytics
  :route:search:contract --> :core:navigation
  :route:search:scene -.-> :core:designsystem
  :route:search:scene -.-> :core:ui
  :route:search:scene -.-> :route:search:contract
  :route:settings:contract --> :core:navigation
  :route:settings:scene -.-> :core:data
  :route:settings:scene -.-> :core:designsystem
  :route:settings:scene -.-> :core:ui
  :route:settings:scene -.-> :route:settings:contract
  :route:test1:contract --> :core:navigation
  :route:test1:scene -.-> :core:designsystem
  :route:test1:scene -.-> :core:ui
  :route:test1:scene -.-> :route:test1:contract
  :route:test2:contract --> :core:navigation
  :route:test2:scene -.-> :core:designsystem
  :route:test2:scene -.-> :core:ui
  :route:test2:scene -.-> :route:test2:contract
  :route:test3:contract --> :core:navigation
  :route:test3:scene -.-> :core:designsystem
  :route:test3:scene -.-> :core:ui
  :route:test3:scene -.-> :route:test3:contract

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-route fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

<details><summary>📋 Graph legend</summary>

```mermaid
graph TB
  application[application]:::android-application
  route[route]:::android-route
  library[library]:::android-library
  jvm[jvm]:::jvm-library

  application -.-> route
  library --> jvm

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-route fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

</details>
<!--endregion-->
