# Architecture Rules

## Modules

### core/common
- 存放 `DataStore<Preferences>` 和用户 token 等，另外多语言文本和图片资源等公共资源也放在这个模块

### core/ui
- 存放所有自定义控件，主题，颜色等相关内容，特别注意，下拉刷新和加载更多控件需要统一使用 GSYPullRefresh

### core/network
- 是网络请求模块 ，网络数据的实体都在这个模块的 model/ 目录下，接口地址是 api/ 下的 GitHubApiService，同时也有 config 配置，比如  PAGE_SIZE

### core/database
- 是所有数据库模块，包括所有数据库能力，有 xxDao、xxEntiny，而每次修改数据库如果设计增删字段，需要修改增加 AppDatabase 的数据库版本

### data
- 模块是数据操作处理，包括所有 mediator/xxxMediator 、xxxRepository ，另外所有数据库的  toEntity 和 toXXX 网络数据的实体，都写在 mapper/DataMapper 内统一处理

### feature
- 模块是页面功能模块，内部每个模块每个模块的页面 xxxScreen 和 xxxViewModel

## 一个常规页面模块结构
- 初始状态使用 GSYGeneralLoadState 加载首次数据
- 加载首次数据时，如果有数据库数据，先加载显示其数据库数据，之后再请求网络数据，网络数据回来后，更新数据库和 UI
- 之后数据的刷新和加载更多，可以通过 GSYPullRefresh 控件实现，由用户自己操作触发

## 注意：
- 工作时注意当前是 windows 环境还是 macOS 环境
- 不允许随意删除我的注释和无用代码
- 所有显示类型的文本内容都要多语言
- 所有模块内代码都是在  src/main/java/packageName/ 下
- 依赖添加和版本修改需要走 gradle/ 下的 libs.versions.toml 进行统一管理
- 创建代码时，要以 libs.versions.toml 里的版本为主，尽量使用正确的 API
- Icons 使用 import androidx.compose.material.icons.Icons / import androidx.compose.material.icons.filled
- 每次修改后，需要注意检查是否有这个修改的关联使用需要同步处理
- 任何修改数据库表的变动，都需要修改增加 AppDatabase 的数据库版本
- 使用控件优先判断 core/ui 有没有合适，没有合适的，考虑添加自定义的控件进去（如果符合通用情况）
