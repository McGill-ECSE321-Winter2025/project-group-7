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
            <input type="text" name="Review Title"><br>
            <label for = "desc"> Description:</label><br>
            <textarea id="desc" name="desc" rows="4" cols="50"> </textarea>
        </form>
        <button>Submit</button>
        <button>Cancel</button>
    </main>
</template>

<script>
</script>

<style>
.star-checkbox input {
    opacity:0;
    
}

.star-checkbox{
    font-size: 100%;
    color:lightgray;
    cursor: pointer;
    transition: color 0.3s;

}

.star-checkbox input:checked {
    color: yellow;

}

</style>