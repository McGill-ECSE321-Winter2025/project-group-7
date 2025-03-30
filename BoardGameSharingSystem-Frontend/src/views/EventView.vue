<template>
    <main>
        <div class="search">
            <input type="search" name="eventName" id="eventSearch" placeholder="Search Events">
            <img id="searchIconImg" src="./myimg.png">
        </div>
        <div class="searchFilters">
            <input type="checkbox" name="FilterByRegistered" id="registeredEventCheckbox">
            <label for="registeredEventCheckbox">Filter By Registered Events</label>
        </div>
        <button id="createEventButton" @click="createEvent">Create An Event</button>
        <table>
            <tr>
                <td class="eventTableDataContainer">
                    <div class="eventBox">
                        <p class="eventName">Event Name</p>
                        <div>    
                            <p class="eventCapacity">8/9</p>
                            <button class="registerToEventButton">Register</button>
                        </div>
                    </div>
                    
                    <div class="eventDetails">
                        <p class="eventDateTime">9AM May 12, 2025 - 10AM May 12, 2025</p>
                        <div class="eventDetailsLeft">
                            <p class="eventLocation">Event Location</p>
                            <p class="eventDescription">Event Descriptionaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa a</p>
                        </div>
                    </div>
                    
                </td>
            </tr>
        </table>
    </main>
</template>

<script>

export default {
  methods: {
    async Test() {
        try {
            console.log("Testing 1 2 3");
        events.forEach((event) => {
            console.log(event.id);
        });
        } catch (e) {
        // TODO: Show a message to the user or something
        console.error(e);
        }
    },
    async createEvent()
    {
        console.log("create an event!");
    },
  }
  // ...
};
</script>

<style scoped>
td{
    border-top: 1px solid black;
    width: 100%;
}
tr:first-child td{
    border-top: none;
}
main{
    flex: 1;
    width: 100%;
    background-color: aquamarine;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 100%;
}
table{
    border-radius: 10px;
    border-style: solid;
    border-color: green;
    width: 60%;
    border-spacing: 0;
    background-color: azure;
    margin-top: 1%;
}
.eventTableDataContainer{
    padding: 1%;
    width: 100%;
}
.eventBox{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: flex-start;
}
.eventBox div{
    display: flex;
}
.eventBox div p {
    margin-right: 10px;
}
.eventDescription{
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
}
button{
    border-radius: 10px;
    border-style: solid;
    background-color: white;
    border-color: grey;
    border-width: 1px;
    padding: 3px;
}
.registerToEventButton{
    color: #32CD32;
    background-color: white;
}
.registerToEventButton:hover{
    background-color: #e3e3e3;
}
.registerToEventButton:active{
    background-color: #bababa;
}
.search{
    margin-top: 1%;
    position: relative;
}
#searchIconImg{
    position: absolute;  
    left: 260px;
    top: 18px;
    width: 16px;
    height: 16px;
    pointer-events: none;
}
#eventSearch{
    height: 50px;
    width: 300px;
    border-radius: 100px;
    border-style: none;
    outline: none;
    text-indent: 20px;
}
</style>