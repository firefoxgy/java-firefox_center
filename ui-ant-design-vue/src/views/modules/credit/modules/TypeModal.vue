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
          
        <a-form-item label="分类名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入分类名称"></a-input>
        </a-form-item>

        <a-form-item label="描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea v-decorator="[ 'detail', validatorRules.detail]" placeholder="请输入描述" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>  
        
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  
  export default {
    name: "credit_TypeModal",
    components: { 

    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"行为分类",
        width:800,
        visible: false, 
        model: {
        },
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
          name:{rules: [{ required: true, message: '请输入分类名称!' }]},
          detail:{rules: [{ required: true, message: '请输入描述!' }]}
        },
        url: {
          save: "/sys/credit/type/save"
        }   
      }
    },
    created () {      

    },
    methods: {
      add () {
        const that = this;
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'name','detail'))
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
                that.$message.success(res.message);
                this.$emit('ok',formData);
              }else{
                that.$message.warning(res.message);
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
