Tool = {

    /**
     * 保存登录用户信息
     */
    setLoginUser: function (loginUser) {
        SessionStorage.set(SESSION_KEY_LOGIN_USER, loginUser);
    },

    /**
     * 获取登录用户信息
     */
    getLoginUser: function () {
        return SessionStorage.get(SESSION_KEY_LOGIN_USER) || "";
    },

    /**
     * 保存登录管理员信息
     */
    setLoginAdmin: function (loginUser) {
        SessionStorage.set(SESSION_KEY_LOGIN_ADMIN, loginUser);
    },

    /**
     * 获取登录管理员信息
     */
    getLoginAdmin: function () {
        return SessionStorage.get(SESSION_KEY_LOGIN_ADMIN) || "";
    },


    /**
     * 保存订单Id信息
     */
    setOrderId: function (loginUser) {
        SessionStorage.set(SESSION_ORDER_ID_KEY, loginUser);
    },

    /**
     * 获取订单Id信息
     */
    getOrderId: function () {
        return SessionStorage.get(SESSION_ORDER_ID_KEY) || "";
    },

    /**
     * 空校验 null或""都返回true
     */
    isEmpty: function (obj) {
        if ((typeof obj == 'string')) {
            return !obj || obj.replace(/\s+/g, "") == ""
        } else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    },

    /**
     * 非空校验
     */
    isNotEmpty: function (obj) {
        return !this.isEmpty(obj);
    }

};
