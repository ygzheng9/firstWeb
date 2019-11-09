var bgGreen = '#57dba2';
var bgBlue = '#0385b8';
var yellow = '#ecdf6c';
var blue = '#0c87eb';
var orange = '#ff5722';
var oranges = [
  '#fff5f2',
  '#ffddd3',
  '#ffbca7',
  '#ff9a7b',
  '#ff7850',
  '#ff4107',
  '#e93600'
];
var COLOR = {
  lightGreen: '#00d7af',
  lightWhite: '#F8F8FF',
  lightGrey: '#E0E0E0',
  lightBlack: '#343a42',
  black: '#000000',
  white: '#ffffff',
  red: '#dc3547',
  blue: '#007bfb',
  yellow: '#ffc108',
  cyan: '#17a2b9',
  grey: '#6c757e',
  green: '#28a748',
  orange: '#ffa500',
  transparent: 'rgba(255, 255, 255, 0)'
};
zeu.Settings.fps = 120;
var heartbeat = new zeu.Heartbeat('heartbeat', {
  viewWidth: 730,
  speed: 1,
  fontColor: COLOR.lightGrey,
  maxQueueCapacity: 50
});
heartbeat.scaleByHeight(150);
setInterval(function() {
  heartbeat.beat({
    color: oranges[getRandomInt(0, oranges.length - 1)],
    space: getRandomInt(0, 10)
  });
}, 500);
var digitalClock = new zeu.DigitalClock('digital-clock', {
  numberColor: orange,
  dashColor: COLOR.transparent
});
digitalClock.scaleByHeight(50);

var barMeters = [];
for (var i = 1; i <= 4; i++) {
  var barMeter = new zeu.BarMeter('bar-meter-' + i, {
    viewWidth: 65,
    dashColor: 'rgba(255, 255, 255, 0.2)',
    barColor: blue,
    gradient: true,
    speed: i * 2,
    space: 8
  });
  barMeters.push(barMeter);
}
setInterval(function() {
  for (var i = 0; i < 6; i++) {
    var value = getRandomInt(50, 100);
    if (barMeters[i] !== undefined) {
      barMeters[i].value = value;
      if (value > 75) {
        barMeters[i].barColor = orange;
      } else {
        barMeters[i].barColor = blue;
      }
    }
  }
}, 1000);
var volumeMeters = [];
for (var i = 1; i <= 4; i++) {
  var volumeMeter = new zeu.VolumeMeter('volume-meter-' + i, {
    viewHeight: 180,
    min: {
      fontColor: COLOR.white,
      value: 0,
      bgColor: COLOR.transparent
    },
    max: {
      fontColor: COLOR.white,
      value: 50,
      bgColor: COLOR.transparent
    },
    bar: {
      borderColor: COLOR.lightGrey,
      fillColor: orange,
      graident: false,
      speed: 10
    },
    marker: {
      bgColor: blue,
      fontColor: COLOR.white
    }
  });
  volumeMeter.scaleTo(0.8, 0.8);
  volumeMeters.push(volumeMeter);
}
updateVolumeMeters();
setInterval(function() {
  updateVolumeMeters();
}, 3000);

function updateVolumeMeters() {
  for (var i = 0; i < volumeMeters.length; i++) {
    volumeMeters[i].value = getRandomInt(18, 26);
  }
}

var roundFans = [];
for (var i = 1; i <= 8; i++) {
  var roundFan = new zeu.RoundFan('round-fan-' + i, {
    fanColor: orange,
    center: {
      color: COLOR.lightGrey,
      bgColor: orange
    },
    speed: getRandomInt(1, 10)
  });
  roundFan.scaleByHeight(80);
  roundFans.push(roundFan);
}
updateFanSpeed();
setInterval(function() {
  updateFanSpeed();
}, 3000);

function updateFanSpeed() {
  for (var i = 0; i < roundFans.length; i++) {
    var speed = getRandomInt(2, 7);
    roundFans[i].speed = speed;
    var color = bgGreen;
    if (speed <= 2) {
      color = bgGreen;
    } else if (speed <= 5) {
      color = blue;
    } else {
      color = orange;
    }
    roundFans[i].fanColor = color;
    roundFans[i].centerBgColor = color;
    var fanRpm = document.getElementById('fan-rpm-' + (i + 1));
    fanRpm.innerHTML = Math.floor((speed * 120) / 60);
    fanRpm.style.color = color;
  }
}

var textBoxes = [];
for (var i = 1; i <= 4; i++) {
  var textBox = new zeu.TextBox('text-box-' + i, {
    text: {
      fontColor: COLOR.white,
      bgColor: oranges[3]
    },
    borderColor: COLOR.transparent,
    bgColor: COLOR.transparent,
    waveColor: COLOR.transparent
  });
  textBox.scaleByHeight(40);
  textBoxes.push(textBox);
}
var messageQueues = [];
for (var i = 1; i <= 4; i++) {
  var messageQueue = new zeu.MessageQueue('message-queue-' + i, {
    viewWidth: 80,
    viewHeight: 200,
    barHeight: 12,
    space: 4,
    speed: 8,
    maxQueueCapacity: 50
  });
  messageQueues.push(messageQueue);
}
setInterval(function() {
  for (var i = 0; i < 4; i++) {
    var mq = messageQueues[i];
    var n = getRandomInt(0, 2);
    if (n == 0) {
      mq.pop();
    } else {
      var color = getRandomInt(0, 2);
      if (color == 0) {
        color = bgGreen;
      } else if (color == 1) {
        color = blue;
      } else {
        color = orange;
      }

      textBoxes[i].textBgColor = color;
      textBoxes[i].borderColor = color;
      mq.push({
        color: color,
        space: getRandomInt(4, 10)
      });
    }
    textBoxes[i].value = mq.queueSize;
  }
}, 300);
var speedCircles = [];
for (var i = 1; i <= 2; i++) {
  var speedCircle = new zeu.SpeedCircle('speed-circle-' + i, {
    text: {
      value: 'ON',
      color: COLOR.white
    },
    circle1: {
      speed: getRandomInt(-1, 1),
      color: COLOR.lightGrey
    },
    circle2: {
      speed: getRandomInt(-1, 1),
      color: bgGreen
    },
    circle3: {
      speed: getRandomInt(-1, 1),
      color: COLOR.white
    },
    circle4: {
      speed: getRandomInt(-1, 1),
      color: oranges[4]
    }
  });
  speedCircle.scaleByHeight(170);
  speedCircles.push(speedCircle);
}
for (var i = 3; i <= 4; i++) {
  var speedCircle = new zeu.SpeedCircle('speed-circle-' + i, {
    text: {
      value: 'HIGH',
      color: COLOR.white
    },
    circle1: {
      speed: 1,
      color: oranges[3]
    },
    circle2: {
      speed: -2,
      color: oranges[2]
    },
    circle3: {
      speed: 3,
      color: oranges[1]
    },
    circle4: {
      speed: -4,
      color: oranges[0]
    }
  });
  speedCircle.scaleByHeight(170);
  speedCircles.push(speedCircle);
}
setInterval(function() {
  for (var i = 0; i < speedCircles.length; i++) {
    speedCircles[i].speed1 = 1 * getRandomInt(1, 3);
    speedCircles[i].speed2 = -2 * getRandomInt(1, 3);
    speedCircles[i].speed3 = 3 * getRandomInt(1, 3);
    speedCircles[i].speed4 = -4 * getRandomInt(1, 3);
  }
}, 3000);
var textMeters = [];
for (var i = 1; i <= 2; i++) {
  var textMeter = new zeu.TextMeter('text-meter-' + i, {
    viewWidth: 400,
    arrowColor: blue,
    displayValue: 'MIDDLE',
    marker: {
      bgColor: yellow,
      fontColor: COLOR.white
    },
    bar: {
      speed: 15,
      fillColor: yellow,
      bgColor: COLOR.lightGrey,
      borderColor: COLOR.transparent
    }
  });
  textMeter.scaleByHeight(60);
  textMeters.push(textMeter);
}
updateTextMeters();
setInterval(function() {
  updateTextMeters();
}, 3000);

function updateTextMeters() {
  for (var i = 0; i < textMeters.length; i++) {
    var value = getRandomInt(45, 75);
    textMeters[i].value = value;
    if (value < 45) {
      textMeters[i].displayValue = 'LOW';
      textMeters[i].fillColor = bgGreen;
      textMeters[i].arrowColor = bgGreen;
      textMeters[i].markerBgColor = bgGreen;
    } else if (value > 70) {
      textMeters[i].displayValue = 'HIGH';
      textMeters[i].fillColor = '#ff4107';
      textMeters[i].arrowColor = '#ff4107';
      textMeters[i].markerBgColor = '#ff4107';
    } else {
      textMeters[i].displayValue = 'MIDDLE';
      textMeters[i].fillColor = blue;
      textMeters[i].arrowColor = blue;
      textMeters[i].markerBgColor = blue;
    }
  }
}

function getRandomColor() {
  return '#' + (((1 << 24) * Math.random()) | 0).toString(16);
}

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function shuffleArray(a) {
  var j = 0;
  var temp = 0;
  for (var i = a.length - 1; i > 0; i--) {
    j = Math.floor(Math.random() * (i + 1));
    temp = a[i];
    a[i] = a[j];
    a[j] = temp;
  }
  return a;
}
