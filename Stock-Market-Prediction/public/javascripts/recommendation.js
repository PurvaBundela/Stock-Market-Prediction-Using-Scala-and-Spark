
function Prediction() {

    $( "#a" ).empty();
// Set the dimensions of the canvas / graph
    var margin = {top: 30, right: 20, bottom: 30, left: 50},
        width = 900 - margin.left - margin.right,
        height = 870 - margin.top - margin.bottom;

// Parse the date / time
    var parseDate = d3.time.format("%m/%d/%Y").parse;
    var formatTime = d3.time.format("%e %B");// Format tooltip date / time

// Set the rangestrann
    var x = d3.time.scale().range([0, width]);
    var y = d3.scale.linear().range([height, 0]);

// Define the axes
    var xAxis = d3.svg.axis().scale(x)
        .orient("bottom").ticks(20);

    var yAxis = d3.svg.axis().scale(y)
        .orient("left").ticks(30);

// Define the line
    var valueline = d3.svg.line()
        .x(function (d) {
            return x(d.date);
        })
        .y(function (d) {
            return y(d.close );
        });

// Define 'div' for tooltips
    var div = d3.select("#a")
        .append("div")  // declare the tooltip div
        .attr("class", "tooltip")
        .style("opacity", 0);

// Adds the svg canvas
    var svg = d3.select("#a")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

// Get the data


    d3.csv("assets/" + $('select#parm1 option:selected').val() + ".csv", function (error, data) {
        data.forEach(function (d) {
            d.date = parseDate(d.date);
            d.close = +d.close;
        });

        // Scale the range of the data
        x.domain(d3.extent(data, function (d) {
            return d.date;
        }));
        y.domain([0, d3.max(data, function (d) {
            return d.close +10 ;
        })]);

        // Add the valueline path.
        svg.append("path")
            .attr("class", "line")
            .attr("d", valueline(data));

        // draw the scatterplot
        svg.selectAll("dot")
            .data(data)
            .enter().append("circle")
            .attr("r", 5)
            .attr("cx", function (d) {
                return x(d.date);
            })
            .attr("cy", function (d) {
                return y(d.close);
            })
            // Tooltip stuff after this
            .on("mouseover", function (d) {
                div.transition()
                    .duration(500)
                    .style("opacity", 0);
                div.transition()
                    .duration(200)
                    .style("opacity", .9);
                div.html(
                    '<a href= "' + d.link + '" target="_blank">' + //with a link
                    formatTime(d.date) +
                    "</a>" +
                    "<br/>" + d.close)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
            });

        // Add the X Axis
        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        // Add the Y Axis
        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis);

    });
}
