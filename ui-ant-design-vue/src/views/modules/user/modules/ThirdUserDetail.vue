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
        <a-descriptions-item label="figureurl" :span="3" style="word-break: break-all">
          <img :src=model.figureurl width="200"/>
          </a-descriptions-item>  
        <a-descriptions-item label="应用id" >{{ model.appId }}</a-descriptions-item>
        <a-descriptions-item label="租户id" :span="2">{{ model.tenantId }}</a-descriptions-item>
        <a-descriptions-item label="sid" >{{ model.sid }}</a-descriptions-item>
        <a-descriptions-item label="login_type" :span="2">{{ model.loginType }}</a-descriptions-item>
        <a-descriptions-item label="externalappid" >{{ model.externalappid }}</a-descriptions-item>
        <a-descriptions-item label="thirdid" :span="2">{{ model.thirdid }}</a-descriptions-item>
        <a-descriptions-item label="third_union_id" >{{ model.thirdUnionId }}</a-descriptions-item>
        <a-descriptions-item label="nickname" >{{ model.nickname }}</a-descriptions-item>     
        <a-descriptions-item label="email">{{ model.email }}</a-descriptions-item>
        <a-descriptions-item label="gender" :span="3">{{ model.gender }}</a-descriptions-item>
        <a-descriptions-item label="country" :span="3">{{ model.country }}</a-descriptions-item>  
        <a-descriptions-item label="province" :span="3">{{ model.province }}</a-descriptions-item>  
        <a-descriptions-item label="city" :span="3">{{ model.city }}</a-descriptions-item>  
        <a-descriptions-item label="reg_from" :span="3">{{ model.regFrom }}</a-descriptions-item>  
        <a-descriptions-item label="version" :span="3">{{ model.version }}</a-descriptions-item>  
        <a-descriptions-item label="创建时间">{{ model.createTime }}</a-descriptions-item>
        <a-descriptions-item label="状态" >
          <a-tag v-if="model.status==-1" color="red">已删除</a-tag>
          <a-tag v-if="model.status==0" color="yellow">禁用</a-tag>
          <a-tag v-else color="green">启用</a-tag>
        </a-descriptions-item>
      </a-descriptions>
  </a-modal>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import { getAction } from '@/api/manage'

  export default {
    name: "user_AppUserDetail",
    components: {

    },
    data () {
      return {
        title:"详情",
        visible: false,
        modalWidth:1080,
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