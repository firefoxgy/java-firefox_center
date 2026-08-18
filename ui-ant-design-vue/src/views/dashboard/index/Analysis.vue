<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="应用" :total=record.app.sum >
        </chart-card>
      </a-col>      

      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="租户" :total=record.tenant.sum >          
        </chart-card>
      </a-col>

      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="用户" :total=record.user.sum >
        </chart-card>
      </a-col>

    </a-row>
  </div>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import RankList from '@/components/chart/RankList'
  import { getAction } from '@/api/manage'
  import Bar from '@/components/chart/Bar'
  import Trend from '@/components/Trend'
  import moment from 'moment'
  import { getWeekStartDate,getWeekEndDate,getMonthStartDate,getMonthEndDate,getQuarterStartDate,getQuarterEndDate,getYearStartDate,getYearEndDate } from "@/utils/dateUtil"

  export default {
    name: "Analysis",
    components: {
      ChartCard,
      RankList,
      Bar,
      Trend
    },
    data() {
      return {
        loading: true,
        dateType: '周',
        indexStyle: 1,
        //日起始
        dayStartDate: moment(new Date()).format('YYYY-MM-DD'),
        dayEndDate: moment(new Date()).format('YYYY-MM-DD'),
        //周起始
        weekStartDate: moment(getWeekStartDate()).format('YYYY-MM-DD'),
        weekEndDate: moment(getWeekEndDate()).format('YYYY-MM-DD'),
        //月起始
        monthStartDate: moment(getMonthStartDate()).format('YYYY-MM-DD'),
        monthEndDate: moment(getMonthEndDate()).format('YYYY-MM-DD'),
        //季起始
        quarterStartDate: moment(getQuarterStartDate()).format('YYYY-MM-DD'),
        quarterEndDate: moment(getQuarterEndDate()).format('YYYY-MM-DD'),
        //年起始
        yearStartDate: moment(getYearStartDate()).format('YYYY-MM-DD'),
        yearEndDate: moment(getYearEndDate()).format('YYYY-MM-DD'),
        start:'',
        end:'',
        record: {
          app: {
            sum: '1'
          },
          tenant: {
            sum: '10'
          },
          user: {
            sum: '100'
          }       
        }
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 600)
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