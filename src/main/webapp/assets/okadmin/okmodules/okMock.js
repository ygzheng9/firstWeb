"use strict";
layui.define([], function(exprots) {
	var okMock = {
		api: {
            login: "http://rap2api.taobao.org/app/mock/233041/login",
            menu: {
                list: "https://easy-mock.com/mock/5d0ce725424f15399a6c2068/okadmin/menu/list"
            },
            user: {
                list: "http://rap2api.taobao.org/app/mock/233041/user/list",
                list2: "http://rap2api.taobao.org/app/mock/233041/user/list2",
                batchNormal: "http://rap2api.taobao.org/app/mock/233041/user/batchNormal",
                batchStop: "http://rap2api.taobao.org/app/mock/233041/user/batchStop",
                batchDel: "http://rap2api.taobao.org/app/mock/233041/user/batchDel",
                add: "http://rap2api.taobao.org/app/mock/233041/user/add",
                edit: "http://rap2api.taobao.org/app/mock/233041/user/edit"
            },
            role: {
                list: "http://rap2api.taobao.org/app/mock/233041/role/list"
            },
            permission: {
                tree: "http://rap2api.taobao.org/app/mock/233041/permission/tree",
                list: "http://rap2api.taobao.org/app/mock/233041/permission/list",
                list2: "http://rap2api.taobao.org/app/mock/233041/permission/list2"
            },
            article: {
                list: "http://rap2api.taobao.org/app/mock/233041/article/list"
            },
			task: {
			    list: "http://rap2api.taobao.org/app/mock/233041/task/list"
			},
			link: {
				list: "http://rap2api.taobao.org/app/mock/233041/link/list"
			},
            product: {
                list: "http://rap2api.taobao.org/app/mock/233041/product/list"
            },
            log: {
                list: "https://easy-mock.com/mock/5d0ce725424f15399a6c2068/okadmin/log/list"
            },
            message: {
                list: "http://rap2api.taobao.org/app/mock/233041/message/list"
            },
			download: {
			    list: "http://rap2api.taobao.org/app/mock/233041/download/list"
			},
			bbs: {
			    list: "http://rap2api.taobao.org/app/mock/233041/bbs/list"
			}
        }
	};
	exprots("okMock", okMock);
});
