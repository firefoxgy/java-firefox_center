<template>
  <a-modal
    :title="title"
    :width="modalWidth"
    :visible="visible"
    :destroyOnClose="true"
    :maskClosable	="true"
    :footer="null"
    @cancel="handleCancel"
    cancelText="关闭">

    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
      </div>
    </template>
      <a-descriptions bordered size="small">
        <a-descriptions-item label="应用名称" :span="3">{{ model.title }}</a-descriptions-item>
        <a-descriptions-item label="应用描述" :span="3">{{ model.detail }}</a-descriptions-item>
        <a-descriptions-item label="client_id" :span="3">{{ model.clientId }}</a-descriptions-item>
        <a-descriptions-item label="client_secret" :span="3">{{ model.clientSecretPre }}</a-descriptions-item>
        <a-descriptions-item label="scope" :span="3">{{ model.scope }}</a-descriptions-item>
        <a-descriptions-item label="authorized_grant_types" :span="3">{{ model.authorizedGrantTypes }}</a-descriptions-item>
        <a-descriptions-item label="access_token_validity" :span="3">{{ model.accessTokenValidity }}</a-descriptions-item>
        <a-descriptions-item label="refresh_token_validity" :span="3">{{ model.refreshTokenValidity }}</a-descriptions-item>
        <a-descriptions-item label="autoapprove" :span="3">{{ model.autoapprove }}</a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="3">{{ model.createTime }}</a-descriptions-item>
        <a-descriptions-item label="更新时间" :span="3">{{ model.updateTime }}</a-descriptions-item>
      </a-descriptions>
  </a-modal>
</template>

<script>
  export default {
    name: "Base_UserDetail",
    components: {
    },
    data () {
      return {
        title:"详情",
        visible: false,
        modalWidth:880,
        model: {}
      }
    },
    created () {

    },
    computed:{
    },
    methods: {
      view (record) {
        this.model={}
        this.qrcode=''          
        this.model=record;
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.disableSubmit = false;
      },
      handleOk () {
        this.close()
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: -8px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>