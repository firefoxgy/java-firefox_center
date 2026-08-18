<template>
  <a-card :bordered="false">
    <a-tabs :defaultActiveKey="activeDicId" @change="changeTab">
      <a-tab-pane 
        v-for="(item) in tabList"
        :key="item.dicId"
        :tab="item.dicName"      
      >
        <a-row>
          <a-col :md="24" :lg="12">
            <config-form :list="formList" @on-submit-success="handleSubmit" :active-dic-id="`${activeDicId}`"></config-form>
          </a-col>
        </a-row>
      </a-tab-pane>             
    </a-tabs>
  </a-card>
</template>
<script>
  import { getConf, saveConf } from '@/api/api'
  import configForm from './modules/configForm'
  export default {
    components: {
      configForm
    },
    data () {
      return {
        activeDicId:'',
        tabList: [],
        tabPropList: [],
        formList: [],
        activeDicId: '1'
      }
    },
    methods: {
      getConf () {
        getConf().then((res) => {
          if (res.success) {
            this.tabList = res.data.tabs
            this.tabPropList = res.data.tabProps
            if (this.tabList.length !== 0) {
              this.formList = []
              this.activeDicId = this.tabList[0].dicId

              res.data.tabProps[0].props.forEach(p => {
                let prop = {}
                prop.name = p.configCode
                prop.type = p.configType
                if (p.configType === 'i-input') {
                  prop.type1 = 'text'
                }
                prop.value = p.configValue
                prop.label = p.configTitle
                this.formList.push(prop)
              })
            }
          } else {
            that.$message.warning(res.msg);
          }
        })
      },
      // table 选中的ID
      changeTab (dicId) {
        this.activeDicId = dicId
        this.formList = []
        this.tabPropList.forEach(item => {
          if (item.dicId === dicId) {
            item.props.forEach(p => {
              let prop = {}
              prop.name = p.configCode
              prop.type = p.configType
              if (p.configType === 'i-input') {
                prop.type1 = 'text'
              }
              prop.value = p.configValue
              prop.label = p.configTitle
              this.formList.push(prop)
            })
          }
        })
      },
      handleSubmit (value) {
        let that = this;
        saveConf(value.data).then((res) => {
          console.log(res);
          if (res.success) {
            that.$message.success(res.msg);
          } else {
            that.$Message.warning(res.msg);
          }
        })
      }
    },
    watch: {
    },
    mounted () {
      this.getConf()
    }
  }
</script>
<style lang="less" scoped>

</style>
