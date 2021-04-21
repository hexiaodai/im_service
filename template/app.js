const App = new Vue({
    el: "#app",
    data: {
        wsUrl: "ws://127.0.0.1:8080/chat?token=152835EB9990F6E75C5BB05A9F87A55C",
        // wsUrl: "ws://127.0.0.1:8080/chat?token=1F9FC0FE3E3BD921CF6ADE6EC95A6B07",
        ws: {},
        msg: 456,
    },

    methods: {
        onSend() {
            this.ws.send(this.msg);
        }
    },
    created() {
        this.ws = new WebSocket(this.wsUrl)
        this.ws.onopen = () => {
            this.ws.send(this.msg);
        }
        this.ws.onmessage = async (result) => {
            console.log(result)
        }
    },

    mounted() {
    }
})