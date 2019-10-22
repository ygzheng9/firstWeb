layui.use(['form', 'layer', 'upload', 'laydate', 'okAddlink'], function () {
    var form = layui.form;
    var upload = layui.upload;
    var $ = layui.jquery;
    var laydate = layui.laydate;
    var $form = $('form');
    var okAddlink = layui.okAddlink.init({
        province: 'select[name=province]',
        city: 'select[name=city]',
        area: 'select[name=area]',
    });

    laydate.render({
        elem: '#uDate', //指定元素
        max: "2019-10-22",
        value: '1999-01-01',
    });

    okAddlink.render();//渲染三级联动省市区

});























