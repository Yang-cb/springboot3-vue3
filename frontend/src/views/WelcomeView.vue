<template>
    <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
        <!--左：图片-->
        <div style="flex: 1">
            <el-image style="width: 100%;height: 100%" fit="cover"
                      src="https://img1.baidu.com/it/u=4097856652,4033702227&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800"/>
        </div>
        <div class="welcome-title">
            <div style="font-size: 30px;font-weight: bold">芦叶满汀洲，寒沙带浅流。</div>
            <div style="margin-top: 10px">柳下系船犹未稳，能几日，又中秋。</div>
            <div style="margin-top: 5px">欲买桂花同载酒，终不似，少年游。</div>
        </div>
        <!--右：表单-->
        <div style="width: 400px;background-color: white">
            <!--路由-->
            <router-view/>
        </div>
    </div>
</template>
<script setup>
</script>
<style scoped>
.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}
</style>