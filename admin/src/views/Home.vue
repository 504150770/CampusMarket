<template>
    <el-row class="home" :getter="20">
        <el-col :span="8" style="margin-top: 20px">
            <el-card shadow="hover">
                <div class="user">
                    <img :src="user.headPic" />
                    <div class="userinfo">
                        <p class="name" v-text="user.username"></p>
                        <p class="access"  v-if="user.roleId === '2'">管理员</p>
                        <p class="access"  v-else-if="user.roleId === '1'">普通用户</p>
                    </div>
                </div>
                <div class="login-info">
                    <p>手机号码：<span v-text="user.phone"></span></p>
                    <p>今日日期：<span v-text="todayDate"></span></p>
                </div>
            </el-card>
            <el-card style="margin-top: 20px; height: 520px" >
                <div style="text-align:center;color: #909399">在线用户列表</div>
                <el-table :data="tableData" height="460">
                    <el-table-column
                            show-overflow-tooltip
                            v-for="(val, key) in tableLabel"
                            :key="key"
                            :prop="key"
                            :label="val">
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
        <el-col :span="16" style="margin-top: 20px">
            <div class="num">
                <el-card
                        shadow="hover"
                        v-for="item in countData"
                        :key="item.name"
                        :body-style="{ display: 'flex', padding: 0 }">
                    <i
                            class="icon"
                            :class="`el-icon-${item.icon}`"
                            :style="{ background: item.color }">
                    </i>
                    <div class="detail">
                        <p class="num"><span v-text="item.value"></span></p>
                        <p class="txt"><span v-text="item.name"></span></p>
                    </div>
                </el-card>
            </div>


            <el-card shadow="hover" style="height: 280px">
                <echart :chartData="echartData.order" isChart="line" style="height: 280px;width:750px;"></echart>
            </el-card>

            <div class="graph">
                <el-card shadow="hover" style="height: 260px">
                    <echart :chartData="echartData.category" style="height: 240px" isChart="pie"></echart>
                </el-card>
            </div>
        </el-col>
    </el-row>
</template>

<script>
    import Echart from "@/components/ECharts.vue";
    export default {
        components: {
            Echart
        },
        data() {
            return {
                user: {},
                tableData: [],
                todayDate: "",
                tableLabel: {
                    id: "编号",
                    username: "昵称"
                },
                countData: [
                    {
                        name: "用户总数",
                        value: 1234,
                        icon: "user",
                        color: "#2ec7c9",
                    },
                    {
                        name: "商品总数",
                        value: 210,
                        icon: "goods",
                        color: "#ffb980",
                    },
                    {
                        name: "订单总数",
                        value: 1234,
                        icon: "money",
                        color: "#5ab1ef",
                    },
                    {
                        name: "今日成交额",
                        value: 1234,
                        icon: "coin",
                        color: "#2ec7c9",
                    },
                    {
                        name: "本周成交额",
                        value: 210,
                        icon: "coin",
                        color: "#ffb980",
                    },
                    {
                        name: "本月成交额",
                        value: 1234,
                        icon: "coin",
                        color: "#5ab1ef",
                    },
                ],
                echartData: {
                    order: {
                        xData: [],
                        series: []
                    },
                    category: {
                        series: []
                    }
                }
            }
        },
        created(){
            let _this = this;
            let token = Tool.getLoginAdmin();
            _this.user.token = token;
            if (Tool.isEmpty(token)) {
                _this.$router.push("/login");
            }else{
                //用token去查询用户数据是否仍存在
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/user/login/get", {token:token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user = resp.data;
                        _this.user.headPic = process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + _this.user.headPic;
                    }else{
                        _this.$router.push("/login");
                    }
                });
            }
        },
        mounted() {
            let _this = this;
            _this.getUserTotal();
            _this.getProductTotal();
            _this.getOrderTotal();
            _this.getTodayPrice();
            _this.getWeekPrice();
            _this.getMonthPrice();
            _this.getOnlineUser();
            _this.echartData.order.xData = [_this.getDate(5), _this.getDate(4), _this.getDate(3), _this.getDate(2), _this.getDate(1)]
            _this.getOrderCountByDateAndState();
            _this.getCategoryListByPrice();
            _this.todayDate = _this.getDate(1);
        },
        methods: {
            getCategoryListByPrice(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/product/category/total-price").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let result = resp.data;
                        let data = [];
                        for(let i=0; i<result.length; i++){
                            data.push({value: result[i].totalPrice, name: result[i].name});
                        }

                        _this.echartData.category.series = [
                            {
                                type: 'pie',
                                data: data
                            }
                        ]
                    }
                });
            },
            getOrderCountByDateAndState(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/count-state-date").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let result = resp.data;
                        _this.echartData.order.series = [
                            {
                                name: '已完成',
                                data: [result[8], result[6], result[4], result[2], result[0]],
                                type: 'line',
                                smooth: true
                            },
                            {
                                name: '未完成',
                                data: [result[9], result[7], result[5], result[3], result[1]],
                                type: 'line',
                                smooth: true
                            }
                        ]
                    }
                });
            },
            getDate(i) {
                let date;
                switch (i) {
                    case 1:
                        // 当前日期
                        date = new Date();
                        break;
                    case 2:
                        // 昨天日期
                        date = new Date(new Date() - 60000*60*24);
                        break;
                    case 3:
                        // 前天日期
                        date = new Date(new Date() - 60000*60*24*2);
                        break;
                    case 4:
                        // 大前天日期
                        date = new Date(new Date() - 60000*60*24*3);
                        break;
                    case 5:
                        // 大大前天日期
                        date = new Date(new Date() - 60000*60*24*4);
                        break;
                }
                let year = date.getFullYear();
                let month = date.getMonth() + 1;
                let strDate = date.getDate();
                if (month >= 1 && month <= 9) {
                    month = "0" + month;
                }
                if (strDate >= 0 && strDate <= 9) {
                    strDate = "0" + strDate;
                }
                let currentDate = year + "年" + month + "月" + strDate + "日";
                return currentDate;
            },
            getOnlineUser(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/user/online").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.tableData = resp.data;
                    }
                });
            },
            getUserTotal(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/user/total").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let total = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '用户总数'){
                                _this.countData[i].value = total + '个';
                            }
                        }
                    }
                });
            },
            getProductTotal(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/product/total").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let total = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '商品总数'){
                                _this.countData[i].value = total + '个';
                            }
                        }
                    }
                });
            },
            getOrderTotal(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/order/total/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let total = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '订单总数'){
                                _this.countData[i].value = total + '个';
                            }
                        }
                    }
                });
            },
            getTodayPrice(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/today-price/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let price = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '今日成交额'){
                                _this.countData[i].value = '￥'+ price;
                            }
                        }
                    }
                });
            },
            getWeekPrice(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/week-price/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let price = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '本周成交额'){
                                _this.countData[i].value = '￥'+ price;
                            }
                        }
                    }
                });
            },
            getMonthPrice(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/month-price/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let price = resp.data;
                        for(let i=0; i<_this.countData.length; i++){
                            if(_this.countData[i].name === '本月成交额'){
                                _this.countData[i].value = '￥'+ price;
                            }
                        }
                    }
                });
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER +  "/system/view_photo?filename=" +img;
            }
        }

    }
</script>

<style lang="scss" scoped>
    @import "~@/assets/scss/home";
</style>
