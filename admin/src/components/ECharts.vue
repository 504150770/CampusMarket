<template>
  <div ref="echart"></div>
</template>

<script>
  import * as echarts from "echarts";

  export default {
  props: {
    chartData: {
      type: Object,
      default() {
        return {
          xData: [],
          series: []
        };
      }
    },
    isChart: {
      type: String,
      default: ""
    }
  },
  watch: {
    chartData: {
      handler: function () {
        let _this = this;
        _this.initChart();
      },
      deep: true,
    }
  },
  mounted(){
      let _this = this;
      _this.initChart();
  },
  computed: {
    options() {
      let _this = this;
      if(_this.isChart === "line"){
          return _this.lineOption;
      }else if(_this.isChart === "bar"){
          return _this.barOption;
      }else if(_this.isChart === "pie"){
        return _this.pieOption;
      }
      return _this.lineOption;
    }
  },
  methods: {
    initChart() {
      let _this = this;
      _this.initChartData();
      // 设置echarts表格了
      if (_this.echart) {
        _this.echart.setOption(_this.options);
      } else {
        _this.echart = echarts.init(_this.$refs.echart);
        _this.echart.setOption(_this.options);
      }
    },
    initChartData() {
        let _this = this;
        if(_this.isChart === 'line'){
           // 初始化折线图数据
           _this.lineOption.xAxis.data = _this.chartData.xData;
           _this.lineOption.series = _this.chartData.series;
        }else if(_this.isChart === 'bar'){
           // 初始化柱状图数据
           _this.barOption.xAxis.data = _this.chartData.xData;
          _this.barOption.series = _this.chartData.series;
        }else if(_this.isChart === 'pie'){
          // 初始化饼状图数据
          _this.pieOption.series = _this.chartData.series;
        }
    }
  },
  data() {
    return {
      lineOption: {
        title: {
          text: '近五天交易次数折线图',
          x: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        color: ['#00EE00', '#FF9F7F'],
        legend: {
          // orient 设置布局方式，默认水平布局，可选值：'horizontal'（水平） ¦ 'vertical'（垂直）
          orient: 'horizontal',
          // x 设置水平安放位置，默认全图居中，可选值：'center' ¦ 'left' ¦ 'right' ¦ {number}（x坐标，单位px）
          x: 'left',
          // y 设置垂直安放位置，默认全图顶端，可选值：'top' ¦ 'bottom' ¦ 'center' ¦ {number}（y坐标，单位px）
          y: 'top',
          data: ['已完成','未完成']
        },
        xAxis: {
          data: []
        },
        yAxis: {},
        series: []
      },
      barOption:{
        title: {
          text: '三个访问量最多商品的柱状图',
          x: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          data: [],
        },
        yAxis: {
        },
        series: [
          {
            type: 'bar',
            data: []
          }
        ]
      },
      pieOption: {
        title: {
          text: '五个最高成交额的商品分类',
          x: 'left'
        },
        tooltip: {
          trigger: 'item',
          formatter: "{b} : {d}%"
        },
        series: [
          {
            type: 'pie',
            data: []
          }
        ]
      },
      echart: null,
    };
  },
};
</script>
