<script setup>

import {ChatDotSquare, Lock, Message, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import router from "@/router";

const form = reactive({
    username: '',
    password: '',
    password_repeat: '', //确认密码
    email: '',
    code: '' //邮箱验证码
})

const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        //表示匹配一个字符，可以是英文字母（大小写不限）、数字或中文字符。
        callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
    } else {
        callback()
    }
}

const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== form.password) {
        callback(new Error("两次输入的密码不一致"))
    } else {
        callback()
    }
}

//电子邮件是否有效
const isEmailValid = ref(false)
//有效的条件
const onValidate = (prop, isValid) => {
    if (prop === 'email')
        isEmailValid.value = isValid
}

const rules = {
    username: [
        {validator: validateUsername, trigger: ['blur', 'change']},
        {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']},
    ],
    password: [
        {required: true, message: '请输入密码', trigger: 'blur'},
        {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
    ],
    password_repeat: [
        {validator: validatePassword, trigger: ['blur', 'change']},
    ],
    email: [
        {required: true, message: '请输入邮件地址', trigger: 'blur'},
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
    ],
    code: [
        {required: true, message: '请输入获取的验证码', trigger: 'blur'},
    ]
}

</script>

<template>
    <div style="text-align: center;margin: 0 20px;">
        <div style="margin-top:100px">
            <div style="font-size: 25px">注册</div>
        </div>

        <div style="margin-top: 30px">
            <el-form :model="form" :rules="rules" @validate="onValidate">
                <el-form-item prop="username">
                    <el-input v-model="form.username" type="text" placeholder="用户名">
                        <template #prefix>
                            <el-icon>
                                <User/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="密码">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password_repeat">
                    <el-input v-model="form.password_repeat" type="password" placeholder="确认密码">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="form.email" type="email" placeholder="电子邮件">
                        <template #prefix>
                            <el-icon>
                                <Message/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-row>
                        <el-col :span="17">
                            <el-input v-model="form.code" type="text" placeholder="电子邮件验证码">
                                <template #prefix>
                                    <el-icon>
                                        <ChatDotSquare/>
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="7">
                            <el-button type="success" :disabled="!isEmailValid" style="text-align: right">获取验证码</el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin-top: 30px">
            <el-button style="width: 270px" type="success" plain>立即注册</el-button>
        </div>

        <el-divider>
            <span style="font-size: 10px;color: gray">已有账号</span>
        </el-divider>

        <div>
            <el-button @click="router.push('/')" style="width: 270px" type="warning" plain>前往登录</el-button>
        </div>
    </div>
</template>

<style scoped>

</style>