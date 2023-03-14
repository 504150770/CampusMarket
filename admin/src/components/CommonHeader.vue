<template>
    <header>
        <div class="l-content">
            <el-button plain icon="el-icon-menu" size="mini" @click="handleMenu"></el-button>

                <h4 style="color:#fff" v-text="getBreadCrumb"></h4>
        </div>
        <div class="r-content">
            <el-dropdown trigger="click" size="mini">
                <span class="el-dropdown-link">
                    <img :src="user.headPic" class="user" />
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </header>
</template>

<script>
    export default {
        data(){
            return{
                user:{
                    headPic: require('@/assets/images/anonymous.png'),
                }
            }
        },
        methods:{
            handleMenu() {
                let _this = this;
                _this.$store.commit("collapseMenu");
            },
            logout(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/user/logout", {token: _this.user.token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user.token = "";
                        SessionStorage.remove(SESSION_KEY_LOGIN_ADMIN);
                        _this.$message.success(resp.msg);
                        _this.$router.push("/login");
                    }
                });
            }
        },
        created(){
            let token = Tool.getLoginAdmin();
            this.user.token = token;
            if (Tool.isEmpty(token)) {
                this.$router.push("/login");
            }else{
                //用token去查询用户数据是否仍存在
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/user/login/get", {token:token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user = resp.data;
                        if(_this.user.headPic === "") {
                            _this.user.headPic = require('@/assets/images/anonymous.png');
                        } else {
                            _this.user.headPic = process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + _this.user.headPic;
                        }
                    }else{
                        _this.$router.push("/login");
                    }
                });
            }
        },
        computed:{
            getBreadCrumb(){
                let _this = this;
                let breadCrumb = "";
                switch(_this.$store.state.tab.breadCrumb.length){
                    case 0:
                        breadCrumb = "首页";
                        break;
                    case 1:
                        breadCrumb = breadCrumb + _this.$store.state.tab.breadCrumb[0];
                        break;
                    case 2:
                        breadCrumb = breadCrumb + _this.$store.state.tab.breadCrumb[0] + " / " + _this.$store.state.tab.breadCrumb[1];
                        break;
                }
                return breadCrumb;
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            }
        }
    }

</script>

<style lang="scss" scoped>
    header {
        display: flex;
        height: 100%;
        align-items: center;
        justify-content: space-between;
    }

    .l-content {
        display: flex;
        align-items: center;
        .el-button {
            margin-right: 20px;
        }
    }

    .r-content {
        .user {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }
    }
</style>
