$(function () {
        function getUniq(list, name) {
            var a = list.map(function (i) {
                return i[name];
            });

            a = _.uniq(a);
            return a;
        }

        var heatChart = echarts.init(document.getElementById("clientPlantHeat"));
        var plants = [];
        var clients = [];

        $.get("/pages/mat/getClientHeat", function (res) {
            // console.log(res);

            var items = res.items;
            plants = getUniq(items, "plant");
            clients = getUniq(items, "client");

            var results = [];
            for (var i = 0; i < plants.length; i++) {
                for (var j = 0; j < clients.length; j++) {
                    var v = _.find(items, {"plant": plants[i], "client": clients[j]});

                    var cnt = 0;
                    if (v !== undefined) {
                        cnt = v.projectCnt;
                    }

                    var a = [i, j, cnt];
                    results.push(a);
                }
            }

            var data = results.map(function (item) {
                return [item[0], item[1], item[2] || '-'];
            });

            var option = {
                title: {
                    top: "top",
                    left: 'center',
                    text: '客户-工厂矩阵',
                    subtext: '生产布局'
                },
                tooltip: {
                    position: 'bottom'
                },
                animation: false,
                grid: {
                    left: '10%',
                    right: '10%',
                    top: '15%',
                    bottom: '5%'
                },
                xAxis: {
                    type: 'category',
                    data: plants,
                    splitArea: {
                        show: true
                    }
                },
                yAxis: {
                    type: 'category',
                    data: clients,
                    splitArea: {
                        show: true
                    }
                },
                visualMap: {
                    min: 0,
                    max: 10,
                    calculable: true,
                    orient: 'horizontal',
                    left: 'center',
                    bottom: '15%',
                    show: false
                },
                series: [{
                    name: 'projectCnt',
                    type: 'heatmap',
                    data: data,
                    label: {
                        normal: {
                            show: true
                        }
                    },
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    tooltip: {
                        formatter: function (param) {
                            var data = param.data;
                            return clients[data[1]] + '@' + plants[data[0]];
                        }
                    }
                }]
            };

            heatChart.setOption(option, true);
            heatChart.on('click', 'series', onClickHeat);

        });

        function onClickHeat(params) {
            var data = params.data;
            var plant = plants[data[0]];
            var client = clients[[data[1]]];
            console.log(client, plant);

            $.get('/pages/mat/getBomList?client=' + client + '&plant=' + plant).done(renderBomList);
        }

        function renderBomList(res) {
            console.log(res);
            $("#bomList").html(res);
            // document.getElementById("bomList").innerHtml = res;
        }


    }
);
