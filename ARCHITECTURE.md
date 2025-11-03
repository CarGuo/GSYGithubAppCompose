# Architecture Rules

## Modules

### core/common
- 存放 `DataStore<Preferences>` 和用户 token 等

### core/ui
- 存放所有自定义控件，主题，颜色等相关内容

### core/network
- 是网络请求模块 ，网络数据的实体都在这个模块的 model/ 目录下，接口地址是 api/ 下的 GitHubApiService

### core/database
- 是所有数据库模块，包括所有数据库能力，有 xxDao、xxEntiny，而每次修改数据库如果设计增删字段，需要修改增加 AppDatabase 的数据库版本

### data
- 模块是数据操作处理，包括所有 mediator/xxxMediator 、xxxRepository ，另外所有数据库的  toEntity 和 toXXX 网络数据的实体，都写在 mapper/DataMapper 内统一处理

### feature
- 模块是页面功能模块，内部每个模块每个模块的页面 xxxScreen 和 xxxViewModel

## 其他注意：
- 所有模块代码都是在  src/main/java/packageName/ 下
- 每次修改后，需要注意检查是否有这个修改的关联使用需要同步处理
- 依赖添加和版本修改需要走 gradle/ 下的 libs.versions.toml 进行统一管理
- 使用控件优先判断 core/ui 有没有合适，没有合适的，考虑添加自定义的控件进去（如果符合通用情况）
