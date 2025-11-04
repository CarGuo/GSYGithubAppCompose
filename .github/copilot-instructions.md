# Architecture Rules

## Modules

开发需求要，要先了解整个项目的结构和可用的模块，记住下方模块的要求和特点

### core/common
- 存放 `DataStore<Preferences>` 和用户 token 等，另外多语言文本（/res/values/ 和 /res/values-zh-rCN）和图片资源等公共资源也放在这个模块

### core/ui
- 存放所有自定义控件，主题，颜色等相关内容，特别注意，下拉刷新和加载更多控件需要统一使用 GSYPullRefresh

### core/network
- 是网络请求模块 ，网络数据的实体都在这个模块的 model/ 目录下，接口地址是 api/ 下的 GitHubApiService，同时也有 config 配置，比如  PAGE_SIZE

### core/database
- 是所有数据库模块，包括所有数据库能力，有 xxDao、xxEntiny，而每次修改数据库如果设计增删字段，需要修改增加 AppDatabase 的数据库版本

### data
- 模块是数据操作处理，包括所有 mediator/xxxMediator 、xxxRepository ，另外所有数据库的  toEntity 和 toXXX 网络数据的实体，都写在 mapper/DataMappers 内统一处理
- data 模块不存放实体 Model , 实体 Model 在 core/network 的 model/ 目录下

### feature
- 模块是页面功能模块，内部每个模块每个模块的页面 xxxScreen 和 xxxViewModel

## 一个常规页面模块结构
- 初始状态使用 GSYGeneralLoadState 加载首次数据
- 使用对应的 xxxRepository 获取数据，Repository 内部会先从数据库获取数据，并且再请求网络数据，更新数据库和 UI
- 之后数据的刷新和加载更多，可以通过 GSYPullRefresh 控件实现，由用户自己操作触发
- 标题栏有 GSYTopAppBar 实现
- 基础页面都会有 Screen 和 ViewModel 实现，ViewModel 会继承 BaseViewModel 
- hilt 现在需要导入的是 androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

## Hilt 使用注意避免：
* 1、Hilt 创建一个 @HiltViewModel 实例时，它首先调用该类的构造函数，并将所有依赖项（如 userRepository）作为参数传入。
* 2.Kotlin 构造顺序: 在 Kotlin 中，当一个子类（ProfileViewModel）被实例化时，其构造过程遵循以下顺序：
  *  子类的构造函数参数被求值。
  *  父类（BaseProfileViewModel）的 init 代码块和构造函数被执行。
  *  子类（ProfileViewModel）的 init 代码块和属性初始化器被执行。
* 所以需要避免在子类的构造函数参数中传递给父类的任何方法调用，这些方法调用依赖于子类的属性，因为这些属性在父类构造期间尚未初始化。

## 导航打开新页面

- 使用 core/ui 下的 GSYNavigator 和 GSYNavHost ，例如  val navigator = LocalNavigator.current

## 注意：
- 工作时注意当前是 windows 环境还是 macOS 环境
- 不允许随意删除我的注释和无用代码
- 所有显示类型的文本内容都要多语言,不能写死,多语言在 core/common模块的 /res/values/ 和 /res/values-zh-rCN 下，需要注意中文和英文两个
- 所有模块内代码都是在  src/main/java/packageName/ 下
- 依赖添加和版本修改需要走 gradle/ 下的 libs.versions.toml 进行统一管理
- 创建代码时，要以 libs.versions.toml 里的版本为主，尽量使用正确的 API
- Icons 使用 import androidx.compose.material.icons.Icons / import androidx.compose.material.icons.filled
- 每次修改后，需要注意检查是否有这个修改的关联使用需要同步处理
- 任何修改数据库表的变动，都需要修改增加 AppDatabase 的数据库版本
- 使用控件优先判断 core/ui 有没有合适，没有合适的，考虑添加自定义的控件进去（如果符合通用情况）
