<script setup>

import {get} from "@/net";
import {useCounterStore} from "@/stores/counter";
import router from "@/router";

const store = useCounterStore()

if (store.auth.user == null) {
    get('/api/user/detail', (data) => {
        store.auth.user = data
        router.push('/index')
    }, () => {
        store.auth.user = null
    })
}

</script>

<template>
    <router-view/>
</template>