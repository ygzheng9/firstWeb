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

## 2019/12/01
1. druidFilter: 如果没有参数，就不再打印语句（jfinal 默认会打印语句）；
1. user/session: 增加全局数据
    1. loginInterceptor 中，先从全局数据中拿，如果全局数据为空，则加载全局数据； 
    2. 登录时，创建 session，刷新全局数据；
1. 权限控制
    1. 页面权限控制：控制显示不显示；
    1. url 权限控制，控制 actionKey 的访问权限； 
    1. 资源 -> 所需角色 <- 用户
1. 自定义指令 Directive
1. Interceptor 进行 url 权限检查；
1. LandingPage 中，不需要 Interceptor 做拦截控制：检查用户登录与否，检查用户权限；           
1. echart 中 click
    1. on series 只能在点击数值时触发，当数值很小时，点不到；
    2. getZr().on 可以在某一列时触发，通过 convertFromPixel 获取 series 的 index；
    
## 2019/12/04
1. upgrade to mysql 8.0 
    1. docker new image
    1. .yml
        1. volumes: mapping host folders to docker image 
        2. ports: mapping docker image port to host 
    2. docker-compose exec db /bin/bash 
    2. mysqldump -h host -P port -u user -p --databases dbname > /xshare/1.sql
    2. mysql -u user -p < /xshare/1.sql  
1. jfinal work with mysql 8
    1. pom new dependencies; 
    2. druid with new connector class; 
    3. default.properties: connect string; 
1. force layout
    1. nodes, categories, links
    1. 节点大小，label 位置，拖动：nodes: name, symbolSize, label, draggable,     
    1. 选中高亮：focusNodeAdjacency, emphasis       
    1. label 显示位置：label.normal.position
    1. legend 显示位置，显示的文字：回调函数 formatter
2. cord 
    1. 元素个数不能太多，60 已经不少了；
    1. 元素可以有大小，类型体现在不同的颜色；
    
## 2019/12/05
1. js 中获取 form 数据，通过 json 发送给后台；
1. axios: 默认都是 json，所以 jfinal 中需要 JsonKit.parse(getRawData(), WkFormDemo.class); 不能直接 getModel/getBean   
1. select 选中 option
    1. select 紧跟着一个 input   
    2. js 中 通过比对 input 和 option 设置选中与否
1. 开始：审批流  

## 2019/12/07
1. 审批流
    1. 跳过的节点，不保留记录；
    1. "已完结" 的审批流，不再响应任何操作；
    1. 当前节点通过，如果没有后续节点，则标记状态为 "已完结"；

## 2019/12/09
1. turbolinks
    1. 所有 js 都放到 head 中；
    2. 如果是绑定事件，那么使用 delegation，绑定到 document 上；--> 只需执行一次；
    3. 如果是和 page 相关初始化 js，那么绑定到 turbolinks:load；
        1. 被绑定函数，必须是 Idemponent 等幂的，也即：重复执行，不改变结果； -> 需要判断是否已经执行过了；
        2. 后续新增加的 js，统一使用 defer，保证作为首页刷新时，dom 都已经完成加载；       
     
## 2019/12/10
1. turbolinks: 
    1. 把所有需要放到 turbolinks:load 中的 fn，注册到全局的 table [{domId, fn}]中，只注册一个 load function；
    2. 在 load function 中做遍历；
    3. 要做到 等幂性：判断 fn 是否已经执行过了，如果没有，才执行，并设置 执行过 的标记；     
    
2. stimulus：
    1. install -> easy. 和 webpack 无缝集成；
    2. 使用方便：只需要一个 startup.js，其中设定 controller 的路径； 
    3. 几个概念：
        1. balaController.hehe
        2. balaController#doit
        3. static targets = ['hehe', 'wowo']
        4. this.heheTarget, this.heheTargets 
        5. this.data.get/set/has
        6. this.element  --> dom    

3. 命名规范
    1. data-controller="relation-vendor-plant"  ==> 是横线分隔
    2. data-controller="relation--vendor-plant"  ==> 如果是 两个横杠，对应的 目录/文件
    3. this.bomListTarget.innerHTML ==> 这里的 this 必须是 class，也即，是 class 的 fn，而不能是 fn 中定义的 fn；        
     
     
## 2019/12/11
1. stimulus
    1. 主要烦恼
        1. 配置要简单，充分利用 turbolinks 的功能；
        1. html 和 js 要关联在一起，作为一个整体（html 和对应的操作 js）；--> vue, react 都是这个路线
        1. html 和 js 代码段，要容易复用；--> 或者 copy，或者 变成同用函数；
        1. 命名规范
            1. 文件名，目录约定；
            1. 代码规范：camelCase, hello_world, helloWorld, hello-world, etc. 
    1. 和 Turbolinks 无缝集成，避免 turbolinks:load 重复调用/不调用 的问题； 
    2. 使用简单，在 webpack 下只需要一个 bootstrap.js 文件，设置 controller 路径即可；
    3. html 中使用 data-controller 来关联 页面相关的 js --> 不再需要在 html 中使用 script 中包含页面相关的 js；
        1. dom 的初始化    --> controller.connect
        2. dom 的事件绑定  --> data-event="click->pie#eat"
        3. dom 的访问     --> 
            1. data-target="pie#place", 
            2. static targets=['place'], this.placeTarget, this.placeTargets
            3. this.pieTarget.innerHTML 
        4. data 属性     
            1. data-pie-size
            1. this.data.get('size') / set('size')
            1. this.element.getAttribut('data-pie-size')
    3. controller lifecycle
        1. initialize: initial class properties; 初始化类中的属性，不涉及 dom 操作；
        2. connect: dom operation，操作 dom；
        3. unconnect: clean up resources，清除资源，比如：timmer；
    1. controller scope 作用域
        1. 在 html 中，data-controller 所包含的 html segment 就是 controller 的 scope
        1. 在 html 中，标注了 data-target 的 dom，在 controller 中可直接访问，而不需要 document.getElementById() 
        1. 当然，controller 中可以使用任何 jquery 代码操作 dom，包括那些不在 scope 内的 dom 都可以；
    3. 遵循 name convention，在规则下组织代码 ==> 类比：制定交通规则后，交通更顺畅
        1. html
            1. data-controller="holiday--amazing-pie", 
            2. data-target="holiday--amazing-pie.size"
            3. data-event="click->holiday--amazing-pie#slice"
        2. js
            1. ./controllers/holiday/amazing_pie_controller.js
            2. static targets = ['pie']
            3. this.pieTarget   --> 第一个匹配的 data-target 的 dom 
            3. this.pieTargets  --> 所有匹配 data-target 的 doms
            4. this.data.get/set/has 
            4. this.element / this.application
            4. this 指的是 js 中 class，只能在 class 的函数中使用，如果在 函数中，又定义了函数，那么 this 就变了；
            
1. upgrade to stimulus
    1. 右上角显示/隐藏菜单的功能；   -> 
    1. 根据 actionKey 高亮菜单；   -> this.xxTarget.classList.toggle('hightlight')
    1. 初始化 dom；               -> 初始化代码，放到 controller.connet() 中执行
    1. dom click event binding； -> data-event="click->pie#eat"
    1. echarts 绘图；             -> controller.connect() 执行
    1. click and load url async  -> this.detailsTarget.innerHTML = html
    1. tab component

## 2019/12/12    
1. nginx 
    1. 做静态资源，启用 gzip
    2. brotli 很难配置；
1. webpack
    1. compress 成 gzip，br
    2. dll
    
## 2019/12/14
1. webpack
    1. jquery, lodash, numeric, axios 放到 externals 中，不使用 webpack 打包；         
2. ramda
    1. 转换 + 数据 
    2. 转换 可以组合 
    3. compose(uniq, map(prop('name')))(items)      
3. zzdom init
         
         
## 2019/12/21
1. bulma calendar 
    1. calendar 和 all 的 css 中有重复，需要手工修改；
    2. calendar 在 js 中需要使用 js 的 Date 对象，而不是 string；      
2. sql template 
    1. 所有的 #para(x) 都不能有 单引号或双引号，因为生成的是 sql 中的 ?，是否需要引号有 db engine 自动判定；
    2. druid plugin 打印出来的 sql，都是没有 引号的，但是实际上是有的；
3. html
    1. 下拉框：每一项 Option 比对 value 属性，设置 selected 属性；
    2. checkbox：对选项，进行 for 循环，在循环内部判断（每一项的 value 属性），设置 checked 属性；   
    3. textarea：不是设置 value 属性，而是设置 innerHTML 属性；注意 html 中的空格/换行；
    4. calendar：在 html 设置 value 即可；
    5. date range：需要通过 js 来进行初始值设置；
    6. 根据 id，设置标题：新增/修改；
    
    
## 2019-12-26
1. 弹出窗口传递信息： page1 --> layer.open(page2)  --> page2 关闭  --> 返回 page1
    1. page1, page2 都是 enjoy 渲染；
    2. page1 给 page2 传参数：页面中拼接 querystring，或 js 中读取 dom 数据，手工 post；
    3. page2 打开时，要么 enjoy 渲染；要么 js 读取约定好的 dom，做客户端的初始化；
    4. page2 操作的结果，需要 js 保存到 page1 的 dom 中（也就是当前页面中，但是不是 page2, 因为 page2 关闭后，会从 dom 中删除）；
    5. layer 判断关闭动作是 "确认"（区别与"取消"），从约定好的 dom 中读取 json string，反序列化成 js object，在给相应的 input 赋值；
    6. page2 选择操作要保存的信息，需要后台输出成 string，保存在 data-values 属性中，通过 getAttribute 一次性读取；
2. 总结：
    1. 两个 html： page1, page2; 
    2. 对应的两个 js;
    3. 后台两个 action：render(page1), render(page2);
    4. page1: js 读取 dom1 属性，拼接 url，post；
    5. 后台：解析参数，render page2; 
    6. page2: js 绑定事件，把当前选中结果信息保存在 dom2 中（需要后台先把数据 render 到当前的 data-values 属性上）；
    7. page1：在 page2 关闭后，从 dom2 取出信息，设置到对应的 dom1 上；         
     
## 2019-12-31
1. modal: 
    1. bulma 中通过设置 class 可以是弹出 modal，所有逻辑都是在 宿主html 中，包括弹出框的信息，
    2. 弹出框的如果有 event，都是在和 宿主html 的 js 在一起的；
    3. 弹出框里的内容，可以通过 ajax 从后台获取 segment，插入到 宿主dom 中，做 modal 显示；      
2. ajax: 
    1. 获取返回的类型：html or json；response.header.content-type
    2. jfinal 中通过 renderToString 把结果保存在 string 中，再作为 json 的属性(和其它属性一起)，返回给前端；   
       
## TODO
1. enjoy 中根据属性，设置 class 属性？已完结状态，btn 不可点击，否则，可点击；
3. 如果执行一系列的 delete/insert/update?
