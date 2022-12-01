<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>lab1</title>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"></script>
    <link rel="stylesheet" href="../CSS/style.css">
    <link rel="stylesheet" href="../CSS/form.css">
</head>

<body>

<!-- particles.js container -->
<div id="particles-js"></div>

<!-- scripts -->
<script src="../js/libs/particles.js"></script>
<script src="../js/libs/app_particular.js"></script>


<header class="header">
    <div class="horizon">
        <div class="info--student">
            Gasyuk Aleksandr P32131
        </div>
        <div class="info--work">
            <div class="info--work__lesson">
                Web-programming
            </div>
            <div class="info--work__lab">
                Lab N2 Variant N1206
            </div>
        </div>
    </div>
    <div id="phone_container" class="horizon">
        <fieldset class="form__group" id="phone_container_form">
            <legend>Share dots to number</legend>
            <div>
                <input type="text" id="choose_number">
            </div>
            <div class="new_error" id="no_dot_selected">
                No dots selected
            </div>
            <div class="new_error" id="user_not_exist">
                This user does not exist
            </div>
            <div>
                <button id="send_dots_button">SEND</button>
            </div>
        </fieldset>
    </div>
</header>


<div class="main">
    <div class="form__container">
        <form class="main__form">
            <fieldset class="form__group">
                <legend>Please select your numbers</legend>
                <div class="group--buttons">
                    <span class="group--text"> X:</span>
                    <input type="radio" id="x1" name="x_param" value="-2">
                    <label for="x1">-2</label>

                    <input type="radio" id="x2" name="x_param" value="-1.5">
                    <label for="x2">-1.5</label>

                    <input type="radio" id="x3" name="x_param" value="-1">
                    <label for="x3">-1</label>

                    <input type="radio" id="x4" name="x_param" value="-0.5">
                    <label for="x4">-0.5</label>

                    <input type="radio" id="x5" name="x_param" value="0" checked>
                    <label for="x5">0</label>

                    <input type="radio" id="x6" name="x_param" value="0.5">
                    <label for="x6">0.5</label>

                    <input type="radio" id="x7" name="x_param" value="1">
                    <label for="x7">1</label>

                    <input type="radio" id="x8" name="x_param" value="1.5">
                    <label for="x8">1.5</label>

                    <input type="radio" id="x9" name="x_param" value="2">
                    <label for="x9">2</label>
                </div>
                <div class="error" id="empty_X">
                    Please select X
                </div>
                <div class="group--buttons">
                    <span class="group--text">  Y: </span><input type="text" id="y_input" maxlength="15">
                </div>
                <div class="error" id="empty_Y">
                    Y cannot be empty
                </div>
                <div class="error" id="not_number_Y">
                    Y must be a number
                </div>
                <div class="error" id="Y_is_out_of_range">
                    Y is out of range (-5; 3)
                </div>
                <div class="group--buttons">
                    <span class="group--text"> R:</span>
                    <input type="checkbox" id="r1" name="r_param" value="1">
                    <label for="r1">1</label>

                    <input type="checkbox" id="r2" name="r_param" value="1.5">
                    <label for="r2">1.5</label>

                    <input type="checkbox" id="r3" name="r_param" value="2">
                    <label for="r3">2</label>

                    <input type="checkbox" id="r4" name="r_param" value="2.5">
                    <label for="r4">2.5</label>

                    <input type="checkbox" id="r5" name="r_param" value="3">
                    <label for="r5">3</label>
                </div>
                <div class="error" id="empty_R">
                    Please select R
                </div>
                <div class="button--sender">
                    <button type="submit" id="submit_button">Submit</button>
                </div>
            </fieldset>
        </form>
    </div>

    <div class="main__container">
        <div class="canvas-container">
            <canvas id="graph">

            </canvas>
        </div>
    </div>
</div>

<div class="table__container">
    <table class="table--main">
        <tbody class="tbody--main">
        <tr>
            <th scope="col">Is in area?</th>
            <th scope="col">X value</th>
            <th scope="col">Y value</th>
            <th scope="col">R value</th>
            <th scope="col">Date</th>
            <th scope="col">Script's time</th>
            <th scope="col">Creator</th>
            <th scope="col" class="choose_col">Select</th>
        </tr>
        </tbody>
    </table>
</div>

<script src="./../js/script.js" defer></script>

</body>

</html>