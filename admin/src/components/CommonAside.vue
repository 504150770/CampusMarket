<template>
    <el-menu :default-active="activeRouter"
             class="el-menu-vertical-demo"
             @open="handleOpen"
             @close="handleClose"
             :collapse="isCollapse"
             background-color="#545c64"
             text-color="#fff"
             active-text-color="#ffd04b"
    >
        <h3 v-show="!isCollapse">后台管理系统</h3>
        <h3 v-show="isCollapse">后台</h3>
        <el-menu-item :index="item.path+''" v-for="item in noChildren" :id="item.path|subPath" :key="item.path" @click="clickMenu(item)">
            <i :class="'el-icon-'+item.icon"></i>
            <span slot="title" v-text="item.label"></span>
        </el-menu-item>
        <el-submenu :index="item.name+''" v-for="item in hasChildren" :key="item.name">
            <template slot="title">
                <i :class="'el-icon-'+item.icon"></i>
                <span slot="title" v-text="item.label"></span>
            </template>
            <el-menu-item-group>
                <el-menu-item :index="subItem.path+''" :id="subItem.path|subPath" v-for="(subItem, subIndex) in item.children" :key="subIndex" @click="clickMenu(subItem)">
                    <i :class="'el-icon-'+subItem.icon"></i>
                    <span slot="title" v-text="subItem.label"></span>
                </el-menu-item>
            </el-menu-item-group>
        </el-submenu>

    </el-menu>
</template>

<style lang="scss" scoped>
    .el-menu {
        height: 100%;
        border: none;
        h3 {
            color: #ffffff;
            text-align: center;
            line-height: 48px;
        }
    }
    .el-menu-vertical-demo:not(.el-menu--collapse) {
        width: 200px;
        height: 100vh;
    }
</style>

<script>
    export default {
        data() {
            return {
                menu:[{
                    path: "/",
                    label: "首页",
                    icon: "s-home",
                    name: "home"
                },
                {
                    label: "用户管理",
                    icon: "user",
                    name: "user",
                    children: [
                        {
                            path: "/user_list",
                            label: "后台用户列表",
                            icon: "document",
                        }
                    ]
                },
                {
                    label: "商品分类管理",
                    icon: "s-operation",
                    name: "category",
                    children: [
                        {
                            path: "/category_list",
                            label: "商品分类列表",
                            icon: "document",
                        }
                    ]
                },
                {
                    label: "商品管理",
                    icon: "goods",
                    name: "product",
                    children: [
                        {
                            path: "/product_list",
                            label: "商品列表",
                            icon: "document",
                        }
                    ]
                },
                {
                    label: "店铺管理",
                    icon: "s-shop",
                    name: "store",
                    children: [
                        {
                            path: "/store_list",
                            label: "店铺列表",
                            icon: "document",
                        }
                    ]
                },
                {
                    label: "订单管理",
                    icon: "money",
                    name: "order",
                    children: [
                        {
                            path: "/order_list",
                            label: "订单列表",
                            icon: "document",
                        }
                    ]
                }],
                activeRouter: ""
            };
        },
        methods: {
            handleOpen(key, keyPath) {
                console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                console.log(key, keyPath);
            },
            clickMenu(item){
                let _this = this;
                let breadCrumb = [];
                let parent = _this.findParent(item);
                if(parent === null){
                    breadCrumb.push(item.label);
                }else{
                    breadCrumb.push(parent.label);
                    breadCrumb.push(item.label);
                }
                _this.$store.state.tab.breadCrumb = breadCrumb;
                _this.$router.push(item.path);
            },
            findParent(child){
                let _this = this;
                let hasChildrenList = _this.hasChildren;
                let i;
                for(i = 0; i < hasChildrenList.length; i++){
                    let childrenList = hasChildrenList[i].children;
                    let j;
                    for(j = 0; j < childrenList.length; j++){
                        if(childrenList[j].path === child.path){
                            return hasChildrenList[i];
                        }
                    }
                }
                return null;
            }
        },
        computed:{
            noChildren(){
                let _this = this;
                return _this.menu.filter((item) => !item.children);
            },
            hasChildren(){
                let _this = this;
                return _this.menu.filter((item) => item.children);
            },
            isCollapse(){
                let _this = this;
                return _this.$store.state.tab.isCollapse;
            }
        },
        filters:{
            subPath(url){
                if(typeof(url) === "undefined" || url === null){
                    return "";
                }
                if(url.length <= 1){
                    return "home";
                }else{
                    return url.substr(1, url.length - 1);
                }
            }
        },
        mounted(){
            let _this = this;
            let url = "";
            if(_this.$route.path.length <= 1){
                url = "home";
            }else{
                url = _this.$route.path.substr(1,_this.$route.path.length-1);
            }
            $("#"+ url).click();
            _this.activeRouter = window.location.pathname;
        }
    }
</script>
