<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="4" :sm="24">
      <behavior-list-left v-model="currentTypeId"/>
    </a-col>
    <a-col :md="24-4" :sm="24">
      <behavior-list-right v-model="currentTypeId"/>
    </a-col>
  </a-row>
</template>

<script>
  import BehaviorListLeft from './modules/BehaviorListLeft'
  import BehaviorListRight from './modules/BehaviorListRight'

  export default {
    name: 'base_DeptList',
    components: { BehaviorListLeft, BehaviorListRight },
    data() {
      return {
        description: '部门管理页面',
        currentTypeId: ''
      }
    },

    methods: {}
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>