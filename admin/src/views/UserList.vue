<template>
    <div id="user-list">
        <div style="margin-top: 20px">
            <el-button type="success" @click="openAdd"><i class="el-icon-document-add"></i> 添加</el-button>
            <el-button type="warning" @click="openEdit"><i class="el-icon-edit-outline"></i>修改</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入用户昵称搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getUserList">搜索</el-button>
        </div>
        <el-table
                ref="multipleTable"
                :data="tableData"
                tooltip-effect="dark"
                style="width: 100%"
                @selection-change="handleSelectionChange">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    prop="id"
                    label="编号"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="username"
                    label="用户昵称"
                    width="200">
            </el-table-column>
            <el-table-column
                    label="用户头像"
                    width="200">
                <template slot-scope="scope">
                    <img :src="scope.row.headPic|filterPhoto" style="width:90px;height:70px;" />
                </template>
            </el-table-column>
            <el-table-column
                    label="所属角色"
                    width="100">
                <template slot-scope="scope">
                    <span v-if="scope.row.roleId === 1">普通用户</span>
                    <span v-if="scope.row.roleId === 2">管理员</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="phone"
                    label="用户手机号"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="email"
                    label="电子邮箱"
                    width="150">
            </el-table-column>
            <el-table-column
                    label="生日日期"
                    width="150">
                <template slot-scope="scope">
                    <span>{{scope.row.birthday.toLocaleString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')}}</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="info"
                    label="个人签名"
                    width="150">
            </el-table-column>
        </el-table>
        <el-pagination
                background
                layout="prev, pager, next"
                :total="pageData.total"
                :page-size="pageData.size"
                :current-page="pageData.page"
                @current-change="changePage"
                style="padding-top:10px;">
        </el-pagination>
        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="用户昵称" label-width="120px">
                    <el-input v-model="form.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户密码" label-width="120px">
                    <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
                </el-form-item>
                <el-form-item label="用户头像" label-width="120px">
                    <input type="file" id="photo-file" style="display:none;" @change="upload">
                    <img :src="showHeadPic" id="photo-view" style="width:100px; height:70px;" />
                    <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                </el-form-item>
                <el-form-item label="手机号码" label-width="120px">
                    <el-input v-model="form.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户性别" label-width="120px">
                    <el-select v-model="form.sex" placeholder="请选择用户性别">
                        <el-option label="男" :value="1"></el-option>
                        <el-option label="女" :value="2"></el-option>
                        <el-option label="未知" :value="3"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="电子邮箱" label-width="120px">
                    <el-input v-model="form.email" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="个人签名" label-width="120px">
                    <el-input v-model="form.info" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="生日日期" label-width="120px">
                    <el-date-picker
                            v-model="form.birthday"
                            type="date"
                            placeholder="请选择生日日期">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="用户角色" label-width="120px">
                    <el-select v-model="form.roleId" placeholder="请选择用户角色">
                        <el-option label="普通用户" :value="1"></el-option>
                        <el-option label="管理员" :value="2"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="saveUser">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                tableData: [],
                multipleSelection: [],
                dialogFormVisible: false,
                title: "",
                showHeadPic: require('@/assets/images/anonymous.png'),
                form: {
                    id: "",
                    username: "",
                    password: "",
                    sex: "",
                    phone: "",
                    roleId: "",
                    headPic: "",
                    email: "",
                    birthday: "",
                    info: "",
                    token: ""
                },
                searchContent: "",
                pageData: {
                    page: 1,
                    size: 5,
                    total: 0
                }
            }
        },
        methods: {
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            openAdd(){
                this.form = {};
                this.form.sex = 3;
                this.showHeadPic = require('@/assets/images/anonymous.png');
                this.dialogFormVisible = true;
                this.title = "添加用户信息";
            },
            openEdit(){
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行修改！");
                    return false;
                }
                this.form = { ...this.multipleSelection[0] };
                if(this.form.headPic === "") {
                    this.showHeadPic = require('@/assets/images/anonymous.png');
                } else {
                    this.showHeadPic = process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + this.form.headPic;
                }
                this.title = "修改用户信息";
                this.dialogFormVisible = true;
            },
            openRemove(){
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行删除！");
                    return false;
                }
                this.form.id = this.multipleSelection[0].id;
                let _this = this;
                this.$confirm('确定要删除编号为：' + this.form.id + ' 的记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    _this.removeUser();
                });
            },
            saveUser(){
                let _this = this;
                this.form.token = Tool.getLoginAdmin();
                this.$ajax.post(process.env.VUE_APP_SERVER + "/user/admin/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.dialogFormVisible = false;
                        _this.$message.success(resp.msg);
                        _this.getUserList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            removeUser(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/user/admin/remove", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getUserList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getUserList(){
                let _this = this;
                let data = {
                    searchContent: this.searchContent,
                    page: this.pageData.page
                };
                this.$ajax.post(process.env.VUE_APP_SERVER + "/user/admin/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.tableData = resp.data.list;
                        _this.pageData.page = resp.data.page;
                        _this.pageData.size = resp.data.size;
                        _this.pageData.total = resp.data.total;
                    }
                });
            },
            changePage(currentPage){
                this.pageData.page = currentPage;
                this.getUserList();
            },
            uploadPhoto(){
                $("#photo-file").click();
            },
            upload(){
                if($("#photo-file").val() === '')return;
                let config = {
                    headers:{'Content-Type':'multipart/form-data'}
                };
                let formData = new FormData();
                formData.append('photo',document.getElementById('photo-file').files[0]);
                let _this = this;
                // 普通上传
                 this.$ajax.post(process.env.VUE_APP_SERVER + "/system/upload_photo", formData, config).then((response)=>{
                     let resp = response.data;
                     if(resp.code === 0){
                         $("#photo-view").attr('src', process.env.VUE_APP_SERVER + '/system/view_photo?filename=' + resp.data);
                         _this.form.headPic = resp.data;
                         _this.$message.success(resp.msg);
                     }else{
                         _this.$message.error(resp.msg);
                     }
                     $("#photo-file").val("");
                 });
            }
        },
        filters:{
            filterPhoto(img){
                if(img !== "") {
                    return process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + img;
                } else {
                    return require('@/assets/images/anonymous.png');
                }
            }
        },
        mounted() {
            $("#photo-view").attr('src', require('@/assets/images/anonymous.png'));
            this.getUserList();
        }
    }
</script>
