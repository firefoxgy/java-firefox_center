<template>
  <div>
    <a-card :loading="loading" :class="{ 'anty-list-cust':true }" :bordered="false">      
      <a-radio-group v-model="dateType" @change="changeDateType">
        <a-radio-button value="日">日</a-radio-button>
        <a-radio-button value="周">周</a-radio-button>
        <a-radio-button value="月">月</a-radio-button>
        <a-radio-button value="季">季</a-radio-button>
        <a-radio-button value="年">年</a-radio-button>
      </a-radio-group>
    </a-card>
    <div style="margin-top:12px;">
      <index-chart ref="indexChat" v-if="indexStyle=='1'"></index-chart>
      <!--<index-bdc ref="indexBdc" v-if="indexStyle=='1'"></index-bdc>-->
    </div>
  </div>
</template>

<script>
  import IndexChart from './IndexChart'
  import IndexBdc from './IndexBdc'
  import moment from 'moment'
  import { getWeekStartDate,getWeekEndDate,getMonthStartDate,getMonthEndDate,getQuarterStartDate,getQuarterEndDate,getYearStartDate,getYearEndDate } from "@/utils/dateUtil"

  export default {
    name: "Analysis",
    components: {
      IndexChart,
      IndexBdc
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
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 600)
    },
    methods: {
      changeDateType(e){
        this.dateType = e.target.value
        switch (this.dateType) {
          case "日":
              this.start=this.dayStartDate;
              this.end=this.dayEndDate;
              break;
          case "周":
              this.start=this.weekStartDate;
              this.end=this.weekEndDate;
              break;
          case "月":
              this.start=this.monthStartDate;
              this.stendart=this.monthEndDate;
              break;
          case "季":
              this.start=this.quarterStartDate;
              this.end=this.quarterEndDate;
              break;
          case "年":
              this.start=this.yearStartDate;
              this.end=this.yearEndDate;
        }
        if(this.indexStyle=="1"){
          this.$refs.indexChat.start=this.start;
          this.$refs.indexChat.end=this.end;
          this.$refs.indexChat.initSum();
        }else{
          
        }
      }
    }
  }
</script>

<style>
  .anty-list-cust .ant-card-body{
    padding: 8px
  }
</style>