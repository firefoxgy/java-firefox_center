<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
          
        <a-form-item label="type" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'type', validatorRules.type]" placeholder="请输入type" :disabled="disabled"></a-input>
        </a-form-item>
        <a-form-item label="conf_key" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'confKey', validatorRules.confKey]" placeholder="请输入confKey" :disabled="disabled"></a-input>
        </a-form-item>
        <a-form-item label="conf_desc" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea v-decorator="[ 'confDesc', {}]" placeholder="请输入描述" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>
        <a-form-item label="conf_value" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'confValue', validatorRules.confValue]" placeholder="请输入conf_value"></a-input>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态">
          <a-switch checkedChildren="启用" unCheckedChildren="禁用" v-model="stateSwitch"/>
        </a-form-item>   
        
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  
  export default {
    name: "config_AllModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"配置",
        width:800,
        visible: false, 
        disabled: false,
        model: {
        },
        stateSwitch:true, //状态 默认启用
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules:{
          type:{rules: [{ required: true, message: '请输入type!' }]},
          confKey:{rules: [{ required: true, message: '请输入confKey!' }]},
          confValue:{rules: [{ required: true, message: '请输入conf_value!' }]}
        },
        url: {
          save: "/sys/config/all/save"
        }  
      }
    },
    created () {
      

    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);      
        if(record.hasOwnProperty("id")){
          this.stateSwitch=record.status==1?true:false;
          this.disabled=true;
        }else{
          this.disabled=false;
        }
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'type','confKey','confDesc','confValue'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证        
        this.form.validateFields((err, values) => {
          if (!err) {  
            that.confirmLoading = true;
            let httpurl = this.url.save;
            let method = 'post';
            let formData = Object.assign(this.model, values);
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.msg);
                this.$emit('ok',formData);
              }else{
                that.$message.warning(res.msg);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
         
        })
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>
