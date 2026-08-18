<template>
  <div>
    <a-card :loading="loading" :class="{ 'anty-list-cust':true }" :bordered="false">      
      <a-radio-group v-model="indexType" @change="changeIndexType">
        <a-radio-button value="日">日</a-radio-button>
        <a-radio-button value="周">周</a-radio-button>
        <a-radio-button value="月">月</a-radio-button>
        <a-radio-button value="季">季</a-radio-button>
        <a-radio-button value="年">年</a-radio-button>
      </a-radio-group>
      <span style="text-align: right;float:right;margin-top:6px;">
        请选择首页样式：
        <a-radio-group v-model="indexStyle">
          <a-radio :value="1">统计图表</a-radio>
          <a-radio :value="2">统计图表2</a-radio>
        </a-radio-group>
      </span>
    </a-card>
    <div style="margin-top:12px;">
      <index-chart v-if="indexStyle==1"></index-chart>
      <index-bdc v-if="indexStyle==2"></index-bdc>      
    </div>
  </div>
</template>

<script>
  import IndexChart from './IndexChart'
  import IndexBdc from './IndexBdc'

  export default {
    name: "Analysis",
    components: {
      IndexChart,
      IndexBdc
    },
    data() {
      return {
        indexTable: "1",
        indexStyle: 1
      }
    },
    created() {

    },
    methods: {

    }
  }
</script>

<style>
  .anty-list-cust .ant-card-body{
    padding: 8px
  }
</style>