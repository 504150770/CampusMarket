<template>
    <div id="order-list">
        <div style="margin-top: 20px">
            <el-button type="success" @click="openEdit"><i class="el-icon-edit-outline"></i>修改状态</el-button>
            <el-button type="warning" @click="openView"><i class="el-icon-view"></i>查看详情</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入订单号搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getOrderList">搜索</el-button>
        </div>
        <el-table
                ref="multipleTable"
                :data="tableData"
                tooltip-effect="dark"
                style="width: 100%"
                @selection-change="handleSelectionChange">
            <el-table-column
                    type="selection"
                    width="50">
            </el-table-column>
            <el-table-column
                    prop="id"
                    label="编号"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="no"
                    label="订单号"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="totalPrice"
                    label="订单金额(元)"
                    width="180" align="center">
            </el-table-column>
            <el-table-column
                    prop="userDTO.username"
                    label="所属用户"
                    width="180">
            </el-table-column>
            <el-table-column
                    label="订单状态"
                    width="180">
                <template slot-scope="scope">
                    <span>{{scope.row.state|filterState}}</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="下单时间"
                    show-overflow-tooltip>
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
        <el-dialog title="修改订单状态信息" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="订单状态" label-width="120px">
                    <el-select v-model="form.state" placeholder="请选择订单状态">
                        <el-option label="待支付" :value="1"></el-option>
                        <el-option label="已支付" :value="2"></el-option>
                        <el-option label="已取消" :value="5"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="saveOrder">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="订单详情信息" :visible.sync="viewDialogFormVisible">
            <span v-text="'订单号：'+order.no"></span><br>
            <span v-text="'收货地址：'+order.address"></span><br>
            <span v-text="'订单备注：'+order.remark"></span>
            <el-table :data="orderItemList">
                <el-table-column property="productName" label="商品名称" width="250"></el-table-column>
                <el-table-column align="center" property="productPrice" label="商品价格(元)" width="100"></el-table-column>
                <el-table-column align="center" property="quantity" label="购买数量" width="100"></el-table-column>
                <el-table-column align="center" label="商品图片" width="100">
                    <template slot-scope="scope">
                        <img :src="scope.row.productPhoto|filterPhoto" style="width:90px;height:70px;" />
                    </template>
                </el-table-column>
                <el-table-column align="center" property="sumPrice" label="小计(元)" width="100"></el-table-column>
                <el-table-column align="center" label="所属店铺" width="150">
                    <template slot-scope="scope">
                        <span>{{scope.row.storeDTO.name}}</span>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="状态" width="100">
                    <template slot-scope="scope">
                        <span>{{scope.row.state|filterState}}</span>
                    </template>
                </el-table-column>
            </el-table>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="viewDialogFormVisible=false">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: 'order-list',
        data(){
            return{
                tableData: [],
                multipleSelection: [],
                dialogFormVisible: false,
                viewDialogFormVisible: false,
                form: {
                    id: "",
                    state: ""
                },
                searchContent: "",
                pageData: {
                    page: 1,
                    size: 5,
                    total: 0
                },
                order: {},
                orderItemList: []
            }
        },
        methods:{
            saveOrder(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/admin/order/edit-state", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success("修改状态成功！");
                        _this.dialogFormVisible = false;
                        _this.getOrderList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            openRemove(){
                let _this = this;
                if(_this.multipleSelection.length !== 1){
                    _this.$message.warning("请选择一条数据进行删除！");
                    return false;
                }
                _this.form.id = _this.multipleSelection[0].id;
                this.$confirm('确定要删除编号为：' + _this.form.id + ' 的记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    _this.removeOrder();
                });
            },
            removeOrder(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/admin/order/remove", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getOrderList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            handleSelectionChange(val) {
                let _this = this;
                _this.multipleSelection = val;
            },
            getOrderList(){
                let _this = this;
                let data = {
                    searchContent: _this.searchContent,
                    page: _this.pageData.page
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/store/admin/order/list", data).then((response)=>{
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
                let _this = this;
                _this.pageData.page = currentPage;
                _this.getOrderList();
            },
            openEdit(){
                let _this = this;
                if(_this.multipleSelection.length !== 1){
                    _this.$message.warning("请选择一条数据进行修改状态！");
                    return false;
                }
                _this.form.id = _this.multipleSelection[0].id;
                _this.form.state = _this.multipleSelection[0].state;
                _this.dialogFormVisible = true;
            },
            openView(){
                if(this.multipleSelection.length !== 1){
                    this.$message.warning("请选择一条数据进行查看详情！");
                    return false;
                }
                this.order = this.multipleSelection[0];
                this.tableData.forEach(item => {
                    if(item.id === this.order.id) {
                        this.orderItemList = item.orderItemDTOList;
                    }
                });
                this.viewDialogFormVisible = true;
            }
        },
        mounted(){
            let _this = this;
            _this.getOrderList();
        },
        filters:{
            filterState(state){
                switch(state){
                    case 1:
                        return '待支付';
                    case 2:
                        return '已支付';
                    case 3:
                        return '已发货';
                    case 4:
                        return '已收货';
                    case 5:
                        return '已取消';
                }
            },
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/system/view_photo?filename=" + img;
            }
        }
    }

</script>
