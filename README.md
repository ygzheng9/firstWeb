## 2019/11/10
1. 单文件使用 gulp + layui模块；
2. 多文件使用 webpack；
3. webpack 有 build 模式，除非必要不要增加配置；
4. webpack.entry 定义多入口，可以有相对路径，对应 output 的目录；
5. 读取 excel，逐行回调解析，全部读取完后回调；
6. java 中 groupby 多个字段（multimap），汇总求和，最大的值和后面 n 个和的比例；


## 2019/11/13
1. 根据属性名，取得 java object 的属性值。使用 
getter.
1. echart 中所需的数据是 `无格式` 的，类似与 db 中的 table 数据；
1. echart 中也可以处理 json array。
1. json 中是有格式数据，在 java 中可以转换成无格式的，也可以在 js 中转换（相当于后台返回了很多无用的结构信息）；
1. dataset 只是额外功能，series.data 才是最本质的；
1. treemap 的 series.data 有固定的格式 {name, value}，其中的 value 可以是单个值，也可以是一个 array，第一个值是大小，后面的可以用来显示颜色。
