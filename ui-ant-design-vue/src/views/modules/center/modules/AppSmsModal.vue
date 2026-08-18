<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="模板"
        >
          <a-select :value="smsId" @change="handleChange">
            <a-select-option :value="-1">未设置</a-select-option>
            <a-select-option v-for="(item, key) in smsList" :key="key" :value="item.id">
              {{ item.signName }}
            </a-select-option>
          </a-select>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { httpAction, getAction } from '@/api/manage'

  export default {
    name: 'SysPositionModal',
    components: {

     },
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        appId: '',
        smsId: -1,
        smsList: {},
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
        },
        url: {
          save: '/sys/config/sms/saveAppSms',
          appSms: '/sys/config/sms/appSms',
          smsList: '/sys/config/sms/listAll',
        },
      }
    },
    created() {
      this.initSmsList();
    },
    methods: {
      show(appId) {
        this.appId=appId;
        this.smsId= -1;
        this.getAppSms();
        this.visible = true
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      getAppSms() {
        const that = this
        getAction(this.url.appSms, {appId: this.appId}).then((res) => {
          if (res.success && !!res.data) {
            this.smsId= res.data.smsId;
          }
        })
      },
      initSmsList() {
        const that = this
        getAction(this.url.smsList).then((res) => {
          if (res.success) {
            that.smsList=res.data;
          } else {
            that.$message.warning(res.msg)
          }
        })
      },
      handleChange(value) {
       this.smsId=value;
      },
      handleOk() {
        const that = this
        // 触发表单验证        
        that.confirmLoading = true
        let method = 'post'
        let formData = {appId: this.appId};
        if(this.smsId===-1){
            that.$message.warning("请选择短信模板")
            that.confirmLoading = false
            return;
        }
        formData.smsId=this.smsId;
        httpAction(this.url.save, formData, method).then((res) => {
          if (res.success) {
            that.$message.success(res.msg)
            that.$emit('ok')
          } else {
            that.$message.warning(res.msg)
          }
        }).finally(() => {
          that.confirmLoading = false
          that.close()
        })
      },
      handleCancel() {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>