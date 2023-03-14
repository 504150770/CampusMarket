export default {
    state: {
        isCollapse: false,
        breadCrumb:[]
    },
    mutations: {
        //左侧栏是否水平展开
        collapseMenu (state) {
            state.isCollapse = !state.isCollapse
        }
    }
}