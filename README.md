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

## 2019/11/21
1. bulma tabs 切换；
2. js 在 parent 层级设定 handle 可以响应 未来新创建的 dom 对象的 事件；
3. ajax 需要配合 getscript 而不是 script tag；
4. 菜单项 除了 json 外， index 中有设定初始页面；

## 2019/11/22
1. tab click 有 html 中的绑定，变成 js 中的事件绑定；
2. js function 和 ()=>{} 对 this 是有不同的，在 jquery 的事件处理函数中，如果需要使用 this，那么使用 function () {}
3. js format number => numeral
4. orderNum 并不是 (plant, vendor),会有重复；
5. echart: tooltips 设置全局 axis，参数是个数组，每个元素包含完整的信息
6. mat -> 多个供应源 -> 每个供应源总金额、占比、单价 ；每个工厂的总金额，单价 -> 工厂的入库明细


## 2019/11/23
1. nav, section, footer
0. container is-fluid
1. columns is-multiline is-1
2. card card-head card-head-title card-content
2. table is-size-7
2. has-text-centered
2. card card-content media media-left media-content card-image


## 2019/11/25
1. 去掉没有主动使用到的 css
2. section foot 的默认 margin 会相互重叠
1. card margin 默认就挺好 
1. 一个 columns 自动换行，和 多个 columns，没有差别
1. gulp 把所有 vendor js concate，但是，lay.js 不能打包，猜测是因为自带的模块机制；
1. gulp 把 css 打包；
1. g2.js 的文件很大，用到时再引入；
1. ajax form, 在 controller 中可以直接通过 get(name) 得到 form value;
1. 通过 setCookie 记录登录状态，并且可以设定 cookie 的有效期；超过有效期后，该 cookie 不可用；
1. 全局 interceptor，检查 cookie 判断登录状态；每次 http 请求，都会把 cookie 带上，无论是 a, ajax, href 都会带上 cookie；
2. table 中的 a link，怎么显示颜色？--> 使用 bulma 覆盖掉 lay 
4. 如何新打开个 tab； --> turbolinks 之后不需要多 tab 了，而且手机更友好； 
1. cookie 和 用户信息：
    1. 检查用户名/密码 时，生成 cookie id 以及 对应的用户信息；
    2. 对应关系存入cache，以及数据库；cookie 返回给 response；
    2. 下次请求来时，通过 interceptor 取出 cookie； 如果取不到 cookie，或 cookie 过期了，重新登录；
    2. 根据 cookie id，从 cache 中找到对应的用户信息，把用户信息存入 resquest 中，供后续 action 使用；
    1. 如果 cache 中没有对应关系，从数据库中加载到 cache；如果数据库中也没有，那么重新登录；
1. cache 使用后，启动速度明显变慢，1.6s -> 6.8s
    1. 可以 cache 任何东西，包括 action
    1. 在 配置文件中，可以针对 key，配置不同的缓存策略；
1. nav 右侧 navbar-end 悬浮下拉菜单；


## 2019/11/27
1. redirect -> forwardAction
2. druid filter to print sql with paramaters; 
3. 在 layout 中增加菜单选中的 js 代码，在 interceptor 中设置 actionKey，写入 html 的 hidden，从 js 读取；
4. 一颗料多个供应商时，查出单价最高/最低，以及单价差异；
5. zhihu:
    1. navbar container nav-menu, navbar container nav-tabs
    1. section container tile box media
    1. tile 纵向排列 is-vertical 有边框 is-parent
    1. 多个 media 之间，会自动有细灰线
    1. section 默认顶部空隙大；card 默认底部间隔小
    1. card card-header card-content
    1. fa fa-fire 设置颜色 color:red 设置与左边字体间距 margin-left
6. 查看明细时，如果超过 300, 则给出提示；

## 2019/11/30
1. 采购分析-按供应商：
    1. 80-20 入库金额+累积占比；
    2. 列表：每个 10% 的供应商数量；
    2. 列表：供应商清单，采购金额，送货工厂数量；
    2. 列表+弹出：送货工厂列表；
1. mysql 中可以通过 @cumsum 的方式，实现累加；在 8.0 中有 window function 可直接实现；
1. java 中通过 loop 实现：逐项累加，全部求和，求累加比例，按累加比例分组；
1. multiMap 用以实现 count(1) group by grade；
1. layer 弹出层很好用 open({content: res})；     

## TODO
1. enjoy 中根据属性，设置 class 属性？
3. 如果执行一系列的 delete/insert/update?
