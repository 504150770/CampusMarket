<template>
    <div id="category-list">
        <div style="margin-top: 20px">
            <el-button type="success" @click="openAdd"><i class="el-icon-document-add"></i>添加</el-button>
            <el-button type="warning" @click="openEdit"><i class="el-icon-edit-outline"></i>修改</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-button type="primary" @click="openView"><i class="el-icon-view"></i>查看详情</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入商品名称搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getProductList">搜索</el-button>
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
                    label="商品名称"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="info"
                    label="商品简介"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="tag"
                    label="商品标签"
                    width="150">
                <template slot-scope="scope">
                    <span v-if="scope.row.tag === 1">95新</span>
                    <span v-if="scope.row.tag === 2">9成新</span>
                    <span v-if="scope.row.tag === 3">8成新</span>
                    <span v-if="scope.row.tag === 4">瑕疵</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="price"
                    label="商品价格(元)"
                    width="120" align="center">
            </el-table-column>
            <el-table-column
                    label="商品图片"
                    width="120">
                <template slot-scope="scope">
                    <img :src="scope.row.photo|filterPhoto" style="width:90px;height:70px;" />
                </template>
            </el-table-column>
            <el-table-column
                    label="商品分类名称"
                    width="120" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.categoryDTO">{{scope.row.categoryDTO.name}}</span>
                    <span v-else>无</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="所属店铺"
                    width="120" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.storeDTO">{{scope.row.storeDTO.name}}</span>
                    <span v-else>无</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="商品状态"
                    width="120" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.state === 1" style="color: orange">待审核</span>
                    <span v-if="scope.row.state === 2" style="color: green">审核通过</span>
                    <span v-if="scope.row.state === 3" style="color: red">审核不通过</span>
                    <span v-if="scope.row.state === 4">已下架</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="上架时间"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="stock"
                    label="商品库存(件)"
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
        <el-dialog title="查看商品详情" :visible.sync="viewDialogFormVisible" width="60%" :close-on-click-modal="false">
            <div class="editor-content-view" v-html="form.content"></div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="viewDialogFormVisible = false">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :title="title" @close="closeDialog" :visible.sync="dialogFormVisible" width="60%" :close-on-click-modal="false">
                <el-form :model="form">
                    <el-form-item label="商品名称" label-width="120px">
                        <el-input  v-model="form.name" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="商品图片" label-width="120px">
                        <input type="file" id="photo-file" style="display:none;" @change="upload">
                        <img :src="showPhoto" id="photo-view" style="width:100px; height:70px;" />
                        <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                    </el-form-item>
                    <el-form-item label="商品价格" label-width="120px">
                        <el-input-number v-model="form.price" :min="0" :controls="false" :precision="2"></el-input-number>
                    </el-form-item>
                    <el-form-item label="商品库存" label-width="120px">
                        <el-input-number v-model="form.stock" :min="0" :controls="false" :precision="0"></el-input-number>
                    </el-form-item>
                    <el-form-item label="商品简介" label-width="120px">
                        <el-input type="textarea" :rows="2" placeholder="请输入商品简介" v-model="form.info"></el-input>
                    </el-form-item>
                    <el-form-item label="商品所属分类" label-width="120px">
                        <el-select v-model="form.categoryId" placeholder="请选择商品分类">
                            <el-option v-for="(item,index) in categoryList" :key="index" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="商品所属店铺" label-width="120px">
                        <el-select v-model="form.storeId" placeholder="请选择所属店铺">
                            <el-option v-for="(item,index) in storeList" :key="index" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="商品状态" label-width="120px">
                        <el-radio v-model="form.state" :label="1">待审核</el-radio>
                        <el-radio v-model="form.state" :label="2">审核通过</el-radio>
                        <el-radio v-model="form.state" :label="3">审核不通过</el-radio>
                        <el-radio v-model="form.state" :label="4">已下架</el-radio>
                    </el-form-item>
                    <el-form-item label="商品标签" label-width="120px">
                        <el-select v-model="form.tag" placeholder="请选择商品标签">
                            <el-option label="95新" :value="1"></el-option>
                            <el-option label="9成新" :value="2"></el-option>
                            <el-option label="8成新" :value="3"></el-option>
                            <el-option label="瑕疵" :value="4"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="商品详情" label-width="120px">
                        <TheEditor ref="editor" :content="form.content" @getContent="getContent"></TheEditor>
                    </el-form-item>

                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="closeDialog">取 消</el-button>
                    <el-button type="primary" @click="saveProduct">确 定</el-button>
                </div>

            </el-dialog>

    </div>
</template>

<script>
    import TheEditor from '../components/TheEditor';
    export default {
        data() {
            return {
                tableData: [],
                multipleSelection: [],
                dialogFormVisible: false,
                viewDialogFormVisible: false,
                title: "",
                showPhoto: require('@/assets/images/default_product.png'),
                form: {
                    id: "",
                    name: "",
                    price: 0,
                    info: "",
                    content: "",
                    categoryId: "",
                    state: 1,
                    stock: 0,
                    photo: "",
                    tag: 1,
                    storeId: ""
                },
                searchContent: "",
                pageData: {
                    page: 1,
                    size: 5,
                    total: 0
                },
                categoryList: [],
                storeList: []
            }
        },
        components: { TheEditor },
        methods: {
            closeDialog() {
                this.dialogFormVisible = false;
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            getContent(val) {
                this.form.content = val;
            },
            openAdd(){
                this.form = {state: 1, price: 0, stock: 0, tag: 1};
                this.title = "添加商品信息";
                this.showPhoto = require('@/assets/images/default_product.png');
                this.dialogFormVisible = true;
                this.$nextTick(() => {
                    this.$refs.editor.clearContent();
                });
            },
            openView() {
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行查看详情！");
                    return false;
                }
                this.form = this.multipleSelection[0];
                this.viewDialogFormVisible = true;
            },
            openEdit(){
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行修改！");
                    return false;
                }
                this.title = "修改商品信息";
                this.form = { ...this.multipleSelection[0] };
                if(this.form.photo === "") {
                    this.showPhoto = require('@/assets/images/default_product.png');
                } else {
                    this.showPhoto = process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + this.form.photo;
                }
                this.dialogFormVisible = true;
                this.$nextTick(() => {
                    this.$refs.editor.htmlContent(this.form.content);
                });
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
                        _this.form.photo = resp.data;
                        _this.$message.success(resp.msg);
                    }else{
                        _this.$message.error(resp.msg);
                    }
                    $("#photo-file").val("");
                });
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
                    _this.removeProduct();
                });
            },
            removeProduct(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/product/admin/remove", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getProductList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            saveProduct(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/product/admin/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.dialogFormVisible = false;
                        _this.$message.success(resp.msg);
                        _this.getProductList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getProductList(){
                let data = {
                    searchContent: this.searchContent,
                    page: this.pageData.page
                };
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/product/admin/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.tableData = resp.data.list;
                        _this.pageData.page = resp.data.page;
                        _this.pageData.size = resp.data.size;
                        _this.pageData.total = resp.data.total;
                    }
                });
            },
            getCategoryList(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/product/category/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.categoryList = resp.data;
                    }
                });
            },
            getStoreList(){
                let _this = this;
                this.$ajax.post(process.env.VUE_APP_SERVER + "/store/state/all", {state: 2}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.storeList = resp.data;
                    }
                });
            },
            changePage(currentPage){
                this.pageData.page = currentPage;
                this.getProductList();
            }
        },
        filters:{
            filterPhoto(img){
                if(img !== "") {
                    return process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + img;
                } else {
                    return require('@/assets/images/default_product.png');
                }
            }
        },
        mounted(){
            $("#photo-view").attr('src', require('@/assets/images/default_product.png'));
            this.getProductList();
            this.getCategoryList();
            this.getStoreList();
        }
    }
</script>
<style lang="scss">

    .editor-content-view {
        border: 3px solid #ccc;
        border-radius: 5px;
        padding: 0 10px;
        margin-top: 20px;
        overflow-x: auto;
    }

    .editor-content-view p,
    .editor-content-view li {
        white-space: pre-wrap; /* 保留空格 */
    }

    .editor-content-view blockquote {
        border-left: 8px solid #d0e5f2;
        padding: 10px 10px;
        margin: 10px 0;
        background-color: #f1f1f1;
    }

    .editor-content-view code {
        font-family: monospace;
        background-color: #eee;
        padding: 3px;
        border-radius: 3px;
    }
    .editor-content-view pre>code {
        display: block;
        padding: 10px;
    }

    .editor-content-view table {
        border-collapse: collapse;
    }
    .editor-content-view td,
    .editor-content-view th {
        border: 1px solid #ccc;
        min-width: 50px;
        height: 20px;
    }
    .editor-content-view th {
        background-color: #f1f1f1;
    }

    .editor-content-view ul,
    .editor-content-view ol {
        padding-left: 20px;
    }

    .editor-content-view input[type="checkbox"] {
        margin-right: 5px;
    }

</style>
