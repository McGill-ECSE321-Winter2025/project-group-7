<template>
    <main>
        <h1>Game Name</h1>
        <form>
            <div class="stars">
                <label for="rating"> Set Rating:</label>

                <label class="star-checkbox">
                <input type = "checkbox" id = "1star" name="1star" value = "1star">
                <font-awesome-icon :icon="['fas', 'star']" />
                </label>

                <label class="star-checkbox">
                <input type = "checkbox" id = "2star" name="2star" value = "2star">
                <font-awesome-icon :icon="['fas', 'star']" />
                </label>

                <label class="star-checkbox">
                <input type = "checkbox" id = "3star" name="3star" value = "3star">
                <font-awesome-icon :icon="['fas', 'star']" />
                </label>

                <label class="star-checkbox">
                <input type = "checkbox" id = "4star" name="4star" value = "4star">
                <font-awesome-icon :icon="['fas', 'star']" />
                </label>

                <label class="star-checkbox">
                <input type = "checkbox" id = "5star" name="5star" value = "5star">
                <font-awesome-icon :icon="['fas', 'star']" />
                </label>
            
            </div>
            <label for="title">Title:</label><br>
            <input type="text" name="Review Title"><br> <br>
            <label for = "desc"> Description:</label><br>
            <textarea id="desc" name="desc" rows="4" cols="50"> </textarea>
            
        </form> <br>
        <button>Submit</button>
        <button>Cancel</button>
    </main>
</template>

<script>
</script>

<style>
body {
    background-color: #eee9e0;

}



h1 {
    color : #FFFFFF;
    font-family: "Times New Roman", Times, serif;
}

input[type="text"], textarea {
    border-radius: 10px;
    box-shadow: 0 0 0 1.5px #000;
    border: 5px solid transparent;

}

.star-checkbox input {
    opacity:0;
    
}

.star-checkbox{
    font-size: 100%;
    color:lightgray;
    cursor: pointer;
    transition: color 0.3s;

}

.star-checkbox :hover + svg {
    color: #d99058;
}

.star-checkbox input:checked + svg {
    color: #d99058;
}

textarea {
    resize : none;
}

</style>