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


## 2019/11/14
1. 按照项目计算零件的复用率；
1. 通过预处理成中间表，而不是子查询，可以极大简化 sql 逻辑；
1. 优先考虑使用 enjoy，而不是 js 脚本，尤其是渲染 table 时，当数据量 1~2 千时；
1. layui 中 table column 的 templet 使用 模板时，模板能访问当前数据，但是不能使用其它 js 的值，不实用；
1. table 中的 a link 没有显示，只有鼠标移动到时才有提示；
1. 区域销售分析：
    1. 区域总销量显示为大小，区域内第一名和后面第 n 名的比例，作为热度 
    2. 选定一个区域后，将其下的所有城市从大到小显示；
1. 零件复用度分析(by项目)
    1. 项目平均复用度(单一值) 
    2. 项目零件复用度(列表)
    3. 项目的零件清单及复用数(列表)
    4. 使用到该零件的项目(列表)
1. 零件复用度分析(by BOM)
    1. 按照复用次数，显示统计图；
    2. 点击"复用 n 次"的柱子，显示零件清单；
    3. 显示选中零件的 BOM 清单；
    4. 显示选中 BOM 下的所有零件清单；(BOM层级显示)
1. 客户项目分析
    1. 显示项目-JIT 工厂对应关系；
    2. 选中一个项目，显示其下的所有 BOM 列表；
    3. 选中一个 BOM，显示 BOM 的层级列表；

## 2019/11/15
1. 根据预处理表，重构 项目-零件 共用性分析；
    1. 原始表：bom_item, bom_project_mapping
    3. 预处理表：project_mat 颗粒度: project + partNum; 
        1. 计算：reused
        2. 逻辑：颗粒度是 partNum，该 partNum 在多少个 project 下使用；
        3. reused = 1 是只在一个 project 下使用的物料，也即，没有共用；
        4. reused >1 是 project 间共用的物料；
    2. 预处理表：project_info 颗粒度: project;
        1. 计算：bomCount，逻辑：project 下的 bom 数量；
        2. 计算：partCount，逻辑：project 下所有的 part 数量；
        3. 计算：reusePartCount，逻辑：project 下复用的 part 数量；也即，project_mat.reused > 1 的数量；
        4. 计算：reuseRate，逻辑：100 * 3 / 2  
2. 命名规则
    1. js/html: 文件名全部小写，使用下划线分割；
    1. 目录尽量少，通过前缀区分不同领域；
    1. action: 和文件名对应，采用 camelCase;    
    
## 2019/11/16
1. js router, 通过 `history.pushState` 记录历史，前进/后退 可改变 地址栏；
2. 路由定义 path, render, 匹配传入的 targeturl 和 path；
3. 匹配的方法是把 path 和 targeturl 都根据 / 拆分后，逐项比较；这样可以处理 path variables；
4. 匹配上之后，调用 route.render(route.params) 取得内容，设置 domid.innerHTML；注意是 HTML，全部大写；  
5. pushState 不触发 hashchange，前进/后退 触发；通过监听 hashchagne，可以改变当前页面显示内容；  


## 2019/11/17
1. seajs, layui 的模块机制，都是使用 url 访问模块，不是本地路径，因为他们都没有预处理的过程，只是动态加载机制；
2. 对模块的 定义/注册/使用 相似：define, exports, use；
3. parcel 最简单，指定：入口目录，output 目录；parcel 做剩下的所有事；
```
    "parcel_dev": "parcel watch ./src/parcel/*.* -d ../assets/js/parcel",
    "parcel_build": "parcel build ./src/parcel/*.* -d ../assets/js/parcel"
``` 

## 2019/11/19
1. QAD 中 PO 不是单据，而是(工厂，供应商)的概念；
2. inbound 的综合分析，采购来源分析；

## 2019/11/20
1. 把数据直接写到 hidden 里，在 js 中读出；


## TODO
1. enjoy 中根据属性，设置 class 属性？
2. table 中的 a link，怎么显示颜色？
3. 如果执行一系列的 delete/insert/update?
