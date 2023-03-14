<template>
    <div id="store-list">
        <div style="margin-top: 20px">
            <el-button type="warning" @click="openEditState"><i class="el-icon-edit-outline"></i>审核</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入店铺分类名称搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getStoreList">搜索</el-button>
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
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="name"
                    label="店铺名称"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="username"
                    label="店主姓名"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="phone"
                    label="联系电话"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="info"
                    label="店铺简介"
                    width="300">
            </el-table-column>
            <el-table-column
                    label="店铺状态"
                    width="100">
                <template slot-scope="scope">
                    <span v-if="scope.row.state === 1" style="color: gray">待审核</span>
                    <span v-if="scope.row.state === 2" style="color: green">审核通过</span>
                    <span v-if="scope.row.state === 3" style="color: red">审核不通过</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="所属用户"
                    width="150">
                <template slot-scope="scope">
                    <span>{{scope.row.userDTO.username || ""}}</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="创建时间"
                    width="200">
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
                <el-form-item label="店铺编号" label-width="120px">
                    <span>{{form.id}}</span>
                </el-form-item>
                <el-form-item label="店铺状态" label-width="120px">
                    <el-select v-model="form.state" placeholder="请选择店铺状态">
                        <el-option label="待审核" :value="1"></el-option>
                        <el-option label="审核通过" :value="2"></el-option>
                        <el-option label="审核不通过" :value="3"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="saveStore">确 定</el-button>
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
                form: {
                    id: "",
                    state: 1,
                },
                title: "",
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
            openEditState(){
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行审核！");
                    return false;
                }
                this.form = { ...this.multipleSelection[0] };
                this.title = "审核店铺信息";
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
                    _this.removeStore();
                });
            },
            saveStore() {
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/store/admin/edit-state", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getStoreList();
                        _this.dialogFormVisible = false;
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            removeStore(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/store/remove", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getStoreList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getStoreList(){
                let _this = this;
                let data = {
                    searchContent: this.searchContent,
                    page: this.pageData.page
                };
                this.$ajax.post(process.env.VUE_APP_SERVER + "/store/admin/list", data).then((response)=>{
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
                this.getStoreList();
            }
        },
        mounted(){
            this.getStoreList();
        }
    }
</script>
