import amqp from "./libs/amqplib/callback_api";

const userID = sessionStorage.getItem('user_id');
const userPhone = sessionStorage.getItem('user_phone_number');

if (!userID) {
    window.location.replace("../index.jsp");
}

let formValueX,
    formValueY,
    formValueR;

let listDots = [];

// test();

function test(){
    var args = process.argv.slice(2);

    if (args.length === 0) {
        console.log("Usage: rpc_client.js num");
        process.exit(1);
    }

    amqp.connect('amqp://localhost', function(error0, connection) {
        if (error0) {
            throw error0;
        }
        connection.createChannel(function(error1, channel) {
            if (error1) {
                throw error1;
            }
            channel.assertQueue('', {
                exclusive: true
            }, function(error2, q) {
                if (error2) {
                    throw error2;
                }
                var correlationId = generateUuid();
                var num = parseInt(args[0]);

                console.log(' [x] Requesting fib(%d)', num);

                channel.consume(q.queue, function(msg) {
                    if (msg.properties.correlationId === correlationId) {
                        console.log(' [.] Got %s', msg.content.toString());
                        setTimeout(function() {
                            connection.close();
                            process.exit(0);
                        }, 500);
                    }
                }, {
                    noAck: true
                });

                channel.sendToQueue('rpc_queue',
                    Buffer.from(num.toString()), {
                        correlationId: correlationId,
                        replyTo: q.queue
                    });
            });
        });
    });

    function generateUuid() {
        return Math.random().toString() +
            Math.random().toString() +
            Math.random().toString();
    }
}

sendDataToServer({
    requestType: 'connMake'
}, function (){})

let inputFormX = document.querySelectorAll("input[name='x_param']");

for (let i = 0; i < inputFormX.length; i++) { //when page start, x is undefined
    inputFormX[i].addEventListener("change", (e) => {
        formValueX = e.target.value;
        let elem = document.querySelector("#empty_X");
        elem.style.display = "none";
    })
}

let inputFormY = document.querySelector("#y_input");

inputFormY.addEventListener("input", (e) => {
    formValueY = e.target.value;
    let elemEmpty = document.querySelector("#empty_Y");
    let elemNum = document.querySelector("#not_number_Y");
    let elemRange = document.querySelector("#Y_is_out_of_range");
    if ((!formValueY || !(formValueY.trim())) && (formValueY !== 0)) {
        elemEmpty.style.display = "block";
        elemNum.style.display = "none";
        elemRange.style.display = "none";
    } else {
        elemEmpty.style.display = "none";
        if (/^-?\d*(\.?\d+)?$/.test(formValueY)) {
            let currY = parseFloat(formValueY)
            if (currY > -5 && currY < 3) {
                elemNum.style.display = "none";
                elemRange.style.display = "none";
            } else {
                elemNum.style.display = "none";
                elemRange.style.display = "block";
            }
        } else {
            elemNum.style.display = "block";
            elemRange.style.display = "none";
        }
    }
})

let inputFormR = document.querySelectorAll("input[name='r_param']");

for (let i = 0; i < inputFormR.length; i++) {
    inputFormR[i].addEventListener("change", (e) => {
        let x = e.target.value;
        if (!formValueR) {
            formValueR = [];
        }
        if (e.target.checked) {
            if (!formValueR.includes(x)) {
                formValueR.push(x);
            }
        } else {
            let index = formValueR.indexOf(x);
            if (index !== -1) {
                formValueR.splice(index, 1);
            }
        }
        let elem = document.querySelector("#empty_R");
        if (formValueR.length === 0) {
            elem.style.display = "block";
        } else {
            elem.style.display = "none";
        }
    })
}

let submit = document.querySelector("#submit_button");

submit.addEventListener("click", submitEvent);

function submitEvent(e) {
    e.preventDefault()
    let newValueX = getX(formValueX);
    let newValueY = getY(formValueY);
    let newValueR = getR(formValueR);

    if (newValueX && (newValueY || newValueY === 0) && newValueR) {
        for (let i = 0; i < newValueR.length; i++) {
            let data = {
                requestType: 'areaReq',
                x: newValueX,
                y: newValueY,
                r: newValueR[i],
                startTime: new Date().getTime(),
                ownerID: userPhone
            }
            sendDataToServer(data, requestToTable);
        }
    }
}

function getX(currX) {
    if ([-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2].includes(parseFloat(currX))) {
        return currX;
    }
    let currFormX = document.querySelectorAll("input[name='x_param']");
    if (currFormX.length !== 9) {
        location.reload();
    } else {
        userError("empty_X");
        return null;
    }
}

function getY(currY) {
    if ((!currY || !(currY.trim())) && (currY !== 0)) {
        if (document.querySelector("#y_input")) {
            userError("empty_Y");
            return null;
        } else {
            location.reload();
        }
    } else {
        currY = currY.trim().replaceAll(",", ".")
        if (/^-?\d*(\.?\d+)?$/.test(currY)) {
            currY = parseFloat(currY)
            if (currY > -5 && currY < 3) {
                return currY
            } else {
                userError("Y_is_out_of_range");
                return null;
            }
        } else {
            if (document.querySelector("#y_input")) {
                userError("not_number_Y");
                return null;
            } else {
                location.reload();
            }
        }
    }
}

function getR(currR) {
    if (!currR) {
        let currFormR = document.querySelectorAll("input[name='r_param']");
        if (currFormR.length !== 5) {
            location.reload();
        } else {
            userError("empty_R");
            return null;
        }
    } else {
        if (Array.isArray(currR)) {
            let isCorrect = true;
            for (let i = 0; i < currR.length; i++) {
                if (![1, 1.5, 2, 2.5, 3].includes(parseFloat(currR[i]))) {
                    isCorrect = false;
                }
            }
            if (isCorrect) {
                return currR;
            } else {
                let currFormR = document.querySelectorAll("input[name='r_param']:checked");
                if (currFormR.length !== 0) {
                    currR = [];
                    for (let i = 0; i < currFormR.length; i++) {
                        currR.push(i.value);
                    }
                    return currR;
                } else {
                    userError("empty_R");
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}

function dotsSetter(response) {
    window.listDots = JSON.parse(response);
    console.log(window.listDots);
}

function sendDataToServer(data, requestHandler) {
    let servletUrl = '/server-1.0-SNAPSHOT/ControllerServlet';
    $.ajax({
        type: 'POST',
        url: servletUrl,
        data: data,
        success: (response) => requestHandler(response)
    });
}

function send(valX, valY, valR) {
    let amqp = require('amqplib/callback_api')
    amqp.connect('amqp://localhost', function (error0, connection) {
        if (error0) {
            throw error0;
        }
        connection.createChannel(function (error1, channel) {
            if (error1) {
                throw error1;
            }
            let queue = "hello";
            let msg = "Hello, world!";

            channel.assertQueue(queue, {
                durable: false
            });

            channel.sendToQueue(queue, Buffer.from(msg));
            console.log(" [x] Sent %s", msg);
        });
        setTimeout (() => {
            connection.close();
            process.exit(0);
        }, 500);
    });
}

function requestToTable(response) {
    console.log(response);
    // if (JSON.parse(response).isCorrect && response) {
    if (response) {
        if (!document.querySelector(".table--main")) {
            if (!document.querySelector(".table__container")) {
                let divTable = document.createElement("div");
                divTable.classList.add("table__container")
                document.body.appendChild(divTable);
            }
            let divTable = document.querySelector(".table__container");
            let table = document.createElement("table");
            table.classList.add("table--main");
            divTable.appendChild(table);
            let new_row = table.insertRow(0);
            new_row.insertCell(0).appendChild(document.createTextNode('Is in area?'));
            new_row.insertCell(1).appendChild(document.createTextNode('X value'));
            new_row.insertCell(2).appendChild(document.createTextNode('Y value'));
            new_row.insertCell(3).appendChild(document.createTextNode('R value'));
            new_row.insertCell(4).appendChild(document.createTextNode('Date'));
            new_row.insertCell(5).appendChild(document.createTextNode('Script\'s time'));
        }
        let table = document.querySelector(".table--main")
        let new_row = table.insertRow(1);
        if (JSON.parse(response).inArea) {
            new_row.insertCell(0).appendChild(document.createTextNode('yes'));
        } else {
            new_row.insertCell(0).appendChild(document.createTextNode('no'))
        }
        new_row.insertCell(1).appendChild(document.createTextNode(JSON.parse(response).x));
        new_row.insertCell(2).appendChild(document.createTextNode(JSON.parse(response).y));
        new_row.insertCell(3).appendChild(document.createTextNode(JSON.parse(response).r));
        new_row.insertCell(4).appendChild(document.createTextNode(JSON.parse(response).date));
        new_row.insertCell(5).appendChild(document.createTextNode((JSON.parse(response).time)));
        new_row.insertCell(6).appendChild(document.createTextNode((JSON.parse(response).owner)));
    } else {
        console.error("input Data is incorrect")
    }
}

function userError(id) {
    let elem = document.querySelector("#" + id);
    elem.style.display = "block";
    setTimeout(() => {
        elem.style.display = "none";
    }, 5000)
}

function canvasEvent(x, y) {
    let r = getR(formValueR);

    if (x && (y || y === 0) && r) {
        for (let i = 0; i < r.length; i++) {
            x = Math.round(x * r[i] * 100) / 100;
            y = Math.round(y * r[i] * 100) / 100;
            let data = {
                requestType: 'areaReq',
                x: x,
                y: y,
                r: r[i],
                startTime: new Date().getTime(),
                ownerID: userPhone
            }
            sendDataToServer(data, requestToTable);
        }
    }
}

const WIDTH = 350;
const HEIGHT = 350;
const DPI_WIDTH = WIDTH * 2;
const DPI_HEIGHT = HEIGHT * 2;
const COLOR = "#FFFFFF"
const RADIUS = 300;
const PADDING = 12;

function canvasDraw(canvas) {
    const ctx = canvas.getContext("2d");
    canvas.style.width = WIDTH + "px";
    canvas.style.height = HEIGHT + "px";
    canvas.width = DPI_WIDTH;
    canvas.height = DPI_HEIGHT;

    function getCursorPosition(canvas, event) {
        const rect = canvas.getBoundingClientRect()
        const x = (event.clientX - rect.left) / (RADIUS / 2) - WIDTH / RADIUS;
        const y = -(event.clientY - rect.top) / (RADIUS / 2) + HEIGHT / RADIUS;
        canvasEvent(x, y);
        console.log(rect);
        console.log(event.clientX);
        console.log(rect.left);
        console.log((event.clientX - rect.left))
    }

    canvas.addEventListener('mousedown', function (e) {
        getCursorPosition(canvas, e)
    })

    // Y axis
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.lineWidth = 4;
    ctx.lineTo(DPI_WIDTH / 2, 0);
    ctx.lineTo(DPI_WIDTH / 2, DPI_HEIGHT);
    ctx.stroke();
    ctx.closePath();

    //X axis
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.lineWidth = 4;
    ctx.lineTo(0, DPI_HEIGHT / 2);
    ctx.lineTo(DPI_WIDTH, DPI_HEIGHT / 2);
    ctx.stroke();
    ctx.closePath();

    //Radius
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.font = "normal 40px Montserrat Light";
    ctx.fillStyle = COLOR;
    ctx.fillText("R", DPI_WIDTH / 2 + RADIUS + PADDING, DPI_HEIGHT / 2 - PADDING);
    ctx.fillText("R/2", DPI_WIDTH / 2 + RADIUS / 2 - 2 * PADDING, DPI_HEIGHT / 2 - PADDING);
    ctx.fillText("R/2", DPI_WIDTH / 2 - RADIUS / 2 - 2 * PADDING, DPI_HEIGHT / 2 - PADDING);
    ctx.fillText("R", DPI_WIDTH / 2 + PADDING, DPI_HEIGHT / 2 - RADIUS - PADDING);
    ctx.fillText("R", DPI_WIDTH / 2 + PADDING, DPI_HEIGHT / 2 + RADIUS + PADDING);
    ctx.closePath();

    //square
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.lineWidth = 4;
    ctx.lineTo(DPI_WIDTH / 2, DPI_HEIGHT - (DPI_HEIGHT / 2 + RADIUS));
    ctx.lineTo(DPI_WIDTH / 2 + RADIUS, DPI_HEIGHT - (DPI_HEIGHT / 2 + RADIUS));
    ctx.lineTo(DPI_WIDTH / 2 + RADIUS, DPI_HEIGHT - (DPI_HEIGHT / 2));
    ctx.stroke();
    ctx.closePath();

    //circle
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.lineWidth = 4;
    for (let i = DPI_HEIGHT / 2; i <= DPI_WIDTH / 2 + RADIUS / 2; i++) {
        ctx.lineTo(i, Math.round(Math.sqrt(RADIUS * RADIUS / 4 - (i - DPI_WIDTH / 2) * (i - DPI_WIDTH / 2)) + DPI_HEIGHT / 2));
    }
    ctx.stroke();
    ctx.closePath();

    //triangle
    ctx.beginPath();
    ctx.strokeStyle = COLOR;
    ctx.lineWidth = 4;
    ctx.lineTo(DPI_WIDTH / 2, DPI_HEIGHT - (DPI_HEIGHT / 2 - RADIUS));
    ctx.lineTo(DPI_WIDTH / 2 - RADIUS / 2, DPI_HEIGHT - (DPI_HEIGHT / 2));
    ctx.stroke();
    ctx.closePath();

}

const canvas = document.querySelector("#graph")

canvasDraw(canvas);