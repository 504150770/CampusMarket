<template>
    <div id="login">
        <el-form
                :model="form"
                status-icon
                ref="form"
                label-width="100px"
                class="login-container"
        >
            <h3 class="login_title">后台系统登录</h3>
            <el-form-item label="昵称" label-width="80px" prop="username" class="username">
                <el-input
                        type="input"
                        v-model="form.username"
                        autocomplete="off"
                        placeholder="请输入昵称"
                ></el-input>
            </el-form-item>
            <el-form-item label="密码" label-width="80px" prop="password">
                <el-input
                        type="password"
                        v-model="form.password"
                        autocomplete="off"
                        placeholder="请输入密码"
                ></el-input>
            </el-form-item>
            <el-form-item class="longin_submit">
                <span style="margin-left: 50px;"><el-button @click="login" type="primary" class="longin_submit">登录</el-button></span>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        name: 'login',
        data(){
            return{
                form: {
                    username: "",
                    password: ""
                }
            }
        },
        methods:{
            login(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/user/admin/login", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        Tool.setLoginAdmin(resp.data.token);
                        _this.$message.success(resp.msg);
                        _this.$router.push("/");
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            }
        }
    }
</script>

<style>
    .login-container {
        border-radius: 15px; /*圆角*/
        background-clip: padding-box; /*背景被裁剪到内边距框*/
        margin: 180px auto; /*边界 距上180px 左右居中*/
        width: 350px; /*表单宽度*/
        padding: 35px 35px 15px 35px; /*填充*/
        background: #fff;
        border: 1px solid #eaeaea; /*边框*/
        box-shadow: 0 0 25px #cac6c6; /*隐形*/
    }

    .login_title {
        margin: 0px auto 40px auto; /*上边界0 下边边界40 左右居中*/
        text-align: center; /*上面仅是标签居中 这里是文字居中*/
        color: #505458;
    }

    .longin_submit {
        margin: 10px auto 0px auto; /*上边界10 下边边界40 左右居中*/
    }
</style>
