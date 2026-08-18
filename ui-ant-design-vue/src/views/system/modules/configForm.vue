<template>
  <a-form :form="form">
    <a-form-item
      v-for="(item) in list"
      :key="`item_${activeDicId}_${item.name}`"
      :label="item.label"
      :labelCol="labelCol" 
      :wrapperCol="wrapperCol"
    >
      <a-input v-if="item.type == 'input'" v-model="valueList[item.name]"/>
      <a-textarea v-if="item.type == 'textarea'" v-model="valueList[item.name]" :auto-size="{ minRows: 5, maxRows: 8 }"/>
      <a-select v-if="item.type==='select'" style="width:354px;" mode="multiple" v-model="valueList[item.name]">
        <a-select-option v-for="(option) in item.options" :key="option.val" :value="option.val">{{option.name}}</a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item>
      <slot name="right-btn"></slot>
      <a-button @click="handleSubmit" type="primary">提交</a-button>
      <a-button @click="handleReset" type="primary" style="margin-left: 8px">重置</a-button>
      <slot name="left-btn"></slot>
    </a-form-item>
  </a-form>
</template>
<script>
export default {
  name: 'configForm',
  data () {
    return {
      itemList: [],
      initValueList: [],
      rules: {},
      valueList: {},
      errorStore: {},
      dicId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      form:this.$form.createForm(this)
    };
  },
  props: {
    list: {
      type: Array,
      default: () => []
    },
    activeDicId: {
      type: String,
      default: ''
    }
  },
  watch: {
    list (newVal) {
      this.setInitValue()
    }
  },
  beforeCreate () {
    // this.form = this.$form.createForm(this);
    // // 通过setTimeout模拟网络请求
    // setTimeout((res) => {
    //   this.form.getFieldDecorator('keys', { initialValue: res.keys, preserve: true });
    //   this.show = true;
    //   this.$nextTick(() => {
    //     this.form.setFieldsValue(res);
    //   });
    // }, 2000, { keys: [0, 1, 2, 3], names: ['窗前明月光', '疑似地上霜', '举头望明月', '低头思故乡'], test: 'test' });
  },
  methods: {
    setInitValue () {
      let valueList = {}
      let initValueList = {}
      let errorStore = {}
      this.list.forEach(item => {
        initValueList[item.name] = item.value
        valueList[item.name] = item.value
        initValueList[item.name] = item.value
        errorStore[item.name] = ''
        if (item.dicId === this.dicId) {
          itemList = item.props
        }
      })
      this.valueList = valueList
      this.initValueList = initValueList
      this.errorStore = errorStore
    },
    handleSubmit () {
      this.$emit('on-submit-success', {
        data: this.valueList
      })
    },
    handleReset () {
      this.valueList = clonedeep(this.initValueList)
    },
    handleFocus (name) {
      this.errorStore[name] = ''
    }
  },
  mounted () {
    this.setInitValue()
  }
};
</script>