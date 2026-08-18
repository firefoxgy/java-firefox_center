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
          
        <a-form-item label="应用名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'title', validatorRules.title]" placeholder="请输入应用名称"></a-input>
        </a-form-item>
        <a-form-item label="应用描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea v-decorator="[ 'detail', {}]" placeholder="请输入应用描述" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>

        <a-form-item label="token有效期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'accessTokenValidity', validatorRules.accessTokenValidity]" :min="1" :max="2592000" />
        </a-form-item>
        
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  
  export default {
    name: "center_AppModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"应用",
        width:800,
        visible: false, 
        model: {
        },
        cStateSwitch:true, //状态 默认启用
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
          title:{rules: [{ required: true, message: '请输入应用名称!' }]},
          detail:{rules: [{ required: true, message: '请输入应用描述!' }]},
          accessTokenValidity:{rules: [{ required: true, message: '请输入token有效期!' }]}
        },
        url: {
          save: "/sys/center/app/save"
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
        if(!record.hasOwnProperty("id")){
          this.model.accessTokenValidity = 43200;
        }
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'title','detail','accessTokenValidity'))
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
