<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="会议室" :total=record.room.sum >
          <div>
            <trend style="margin-right: 16px;">
              <span slot="term">已结束</span><a-tag color="#777">{{record.room.end}}</a-tag>
            </trend>
            <trend style="margin-right: 16px;">
              <span slot="term">进行中</span><a-tag color="#00a65a">{{record.room.ing}}</a-tag>
            </trend>
            <trend>
              <span slot="term">未开始</span><a-tag color="#dd4b39">{{record.room.unstart}}</a-tag>
            </trend>
          </div>
          <template slot="footer">日均场次<a-tag color="#00c0ef">{{record.room.day}}</a-tag></template>
        </chart-card>
      </a-col>      

      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="访客" :total=record.visitor.sum >
          <div>
            <trend style="margin-right: 16px;">
              <span slot="term">已到访</span><a-tag color="#777">{{record.visitor.end}}</a-tag>
            </trend>
            <trend style="margin-right: 16px;">
              <span slot="term">今日到访</span><a-tag color="#00a65a">{{record.visitor.today}}</a-tag>
            </trend>
          </div>
          <template slot="footer">日均访客<a-tag color="#00c0ef">{{record.visitor.day}}</a-tag></template>
        </chart-card>
      </a-col>

      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="用户建议" :total=record.suggest.sum >
          <template slot="footer">日均建议数<a-tag color="#00c0ef">{{record.suggest.day}}</a-tag></template>
        </chart-card>
      </a-col>

      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="物管报修" :total=record.repair.sum >
          <template slot="footer">日均报修数<a-tag color="#00c0ef">{{record.repair.day}}</a-tag></template>
        </chart-card>
      </a-col>

    </a-row>

    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
          <a-tab-pane loading="true" tab="访客" key="1">
            <a-row>
              <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
                <bar title="过去15天访客统计图" :dataSource="visitorData"/>
              </a-col>
              <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="最新访客" :list="visitorList"/>
              </a-col>
            </a-row>
          </a-tab-pane>
          <a-tab-pane tab="会议室" key="2">
            <a-row>
              <a-col :xl="14" :lg="10" :md="8" :sm="24" :xs="24">
                <bar title="过去15天会议室统计图" :dataSource="roomData"/>
              </a-col>
              <a-col :xl="10" :lg="14" :md="16" :sm="24" :xs="24">
                <rank-list title="最近会议" :list="roomList"/>
              </a-col>
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-card>

  </div>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import RankList from '@/components/chart/RankList'
  import { getAction } from '@/api/manage'
  import Bar from '@/components/chart/Bar'
  import Trend from '@/components/Trend'
  import moment from 'moment'
  import { getWeekStartDate,getWeekEndDate } from "@/utils/dateUtil"

  export default {
    name: "IndexChart",
    components: {
      ChartCard,
      RankList,
      Bar,
      Trend
    },
    data() {
      return {
        loading: true,
        visitorData: [],
        visitorList: [],
        roomData: [],
        roomList: [],        
        record: {
          room: {
            sum: '0场',
            end: '0',
            ing: '0',
            unstart: '0',
            day: '0',
          },
          visitor: {
            sum: '0人',
            end: '0',
            today: '0',
            day: '0',
          },
          suggest: {
            sum: '0条',
            day: '0',
          },
          repair: {
            sum: '0次',
            day: '0',
          }          
        },
        start:moment(getWeekStartDate()).format('YYYY-MM-DD'),
        end:moment(getWeekEndDate()).format('YYYY-MM-DD'),
        url: {
          sum: '/sys/dashboard/sum',
          chart: '/sys/dashboard/chart',
        }
      }
    },
    created() {
      this.initSum();
      this.initChart();
      setTimeout(() => {
        this.loading = !this.loading
      }, 600)
    },
    methods: {
      initSum(){
        let that=this;
        getAction(this.url.sum,{start:this.start, end:this.end}).then(res=>{
          if(res.success){
            that.record.room.sum=res.data.roomSum+"场";
            that.record.room.end=res.data.roomEnd;
            that.record.room.ing=res.data.roomIng;
            that.record.room.unstart=res.data.roomUnstart;
            that.record.room.day=res.data.roomDay;

            that.record.visitor.sum=res.data.visitorSum+"人";
            that.record.visitor.end=res.data.visitorEnd;
            that.record.visitor.today=res.data.visitorToday;
            that.record.visitor.day=res.data.visitorDay;

            that.record.suggest.sum=res.data.suggestSum+"条";
            that.record.suggest.day=res.data.suggestDay;

            that.record.repair.sum=res.data.repairSum+"次";
            that.record.repair.day=res.data.repairDay;
          }else{
            this.$message.warning(res.msg)
          }
        })
      },
      initChart(){
        let that=this;
        getAction(this.url.chart,{}).then(res=>{
          if(res.success){
            res.data.vChartData.forEach(item => {
              this.visitorData.push({
                x: item.day,
                y: item.cnum
              })
            })
            res.data.vListData.forEach(item => {
              this.visitorList.push({
                name: item.dva_name,
                total: moment(item.create_time).format('YYYY-MM-DD HH:mm')
              })
            })

            res.data.RoomChartData.forEach(item => {
              this.roomData.push({
                x: item.day,
                y: item.cnum
              })
            })
            res.data.RoomListData.forEach(item => {
              this.roomList.push({
                name: item.room_name,
                total: moment(item.start_time).format('YYYY-MM-DD HH:mm')+"至"+moment(item.end_time).format('YYYY-MM-DD HH:mm')
              })
            })
          }else{
            this.$message.warning(res.msg)
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .circle-cust{
    position: relative;
    top: 28px;
    left: -100%;
  }
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

    &.center {
      text-align: center;
      padding: 0 32px;
    }

    span {
      color: rgba(0, 0, 0, .45);
      display: inline-block;
      font-size: .95rem;
      line-height: 42px;
      margin-bottom: 4px;
    }
    p {
      line-height: 42px;
      margin: 0;
      a {
        font-weight: 600;
        font-size: 1rem;
      }
    }
  }
</style>
