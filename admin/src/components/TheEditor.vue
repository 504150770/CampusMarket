<template>
    <div style="width: 98%" id="editor-content">

    </div>
</template>

<script>
    import E from 'wangeditor';
    var editor = null;
    export default {
        props: {
            content: String
        },
        data: function() {
            return {
                editorContent: this.content
            };
        },
        mounted() {
            editor = null;
            this.initEditor();
        },
        methods: {
            htmlContent(val) {
                editor.txt.html(val);
            },
            clearContent() {
                editor.txt.html('');
            },
            initEditor() {
                if (editor === null) {
                    editor = new E(document.getElementById('editor-content'));
                    const _this = this;
                    editor.config.excludeMenus = [
                        'video',
                        'undo',
                        'emoticon'
                    ];
                    editor.config.onchange = function(newHtml) {
                        _this.$emit('getContent', newHtml);
                    };
                    editor.config.uploadImgMaxSize = 10 * 1024 * 1024; // 1M
                    editor.config.uploadImgAccept = ['jpg', 'jpeg', 'png', 'gif'];
                    editor.config.uploadImgMaxLength = 1;
                    editor.config.showLinkImg = false;
                    editor.config.customUploadImg = function (resultFiles, insertImgFn) {
                        let config = {
                            headers:{'Content-Type':'multipart/form-data'}
                        };
                        let formData = new FormData();
                        formData.append('photo',resultFiles[0]);
                        // 普通上传
                        _this.$ajax.post(process.env.VUE_APP_SERVER + "/system/upload_photo", formData, config).then((response)=>{
                            let resp = response.data;
                            if(resp.code === 0){
                                _this.$message.success(resp.msg);
                                insertImgFn(process.env.VUE_APP_SERVER + '/system/view_photo?filename=' + resp.data)
                            }else{
                                _this.$message.error(resp.msg);
                            }
                        });
                    };
                    editor.create();
                    editor.txt.html(this.editorContent);
                }
            }
        }
    };
</script>
