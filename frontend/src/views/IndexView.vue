<script setup>
import {get} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const logout = () => {
    get('/api/auth/logout', (data) => {
        ElMessage.success(data)
        router.push('/')
    })
}
</script>

<template>
    <div>
        index
    </div>
    <div>
        <el-button @click="logout()" type="danger">退出登录</el-button>
    </div>
</template>

<style scoped>

</style>