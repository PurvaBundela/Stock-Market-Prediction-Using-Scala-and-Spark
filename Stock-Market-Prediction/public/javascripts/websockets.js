function WebSocketTest()
         {
               // Let us open a web socket
               var ws = new WebSocket("ws://localhost:9000/echo");

               ws.onopen = function()
               {
                  // Web Socket is connected, send data using send()run
                  //alert($('#texttokafka').val());
                  var p1 = $("#parm1").val();
                  var p2 = $("#parm2").val();
                  var p3 = $("#parm3").val();
                  ws.send(p1+p2+p3);
               };

               ws.onmessage = function (evt)
               {
                  var received_msg = evt.data;

                  var data = jQuery.parseJSON(received_msg);
                                    alert(data.length);
                  for(i=0;i<data.length;i++){
                  //console.log("image "+data[i][2])

                  if(data[i][2] != null && data[i][2] !== undefined && data[i][1] !=null && data[i][1] !== undefined){
                  $("#d"+i+"6").attr("data-l_id",data[i][0]);
                  $("#d"+i+"1").removeClass("hidden");
                   $("#d"+i+"3").attr("src",data[i][2]);
                 $("#d"+i+"5").html(data[i][1])
                }
                  //console.log("content " + data[i][1])
                  }

//                  for(i=0;i<data.length;i++){
//                  var isUndefined = data[i][1] === "undefined"
//                  if(!isUndefined){
//                        $("#demo").append('<div class="col-md-3 col-sm-6 hero-feature">')
//                        $("#demo").append('<div>')
//                        $("#demo").append('<img src="'+data[i][2]+'" alt="">')
//                        $("#demo").append('<div class="caption">')
//                        $("#demo").append('<h3>Feature Label</h3>')
//                        $("#demo").append('<p>'+data[i][1]+'</p>')
//                        $("#demo").append('<p> <a href="#" class="btn btn-primary">Review analysis</a>')
//                        $("#demo").append('</div></div></div>')
//                        }
//                  }

//                                      <div class="col-md-3 col-sm-6 hero-feature">
//                                          <div class="thumbnail">
//                                              <img src="http://placehold.it/800x500" alt="">
//                                              <div class="caption">
//                                                  <h3>Feature Label</h3>
//                                                  <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.</p>
//                                                  <p>
//                                                      <a href="#" class="btn btn-primary">Buy Now!</a> <a href="#"
//                                                                                                          class="btn btn-default">More
//                                                      Info</a>
//                                                  </p>
//                                              </div>
//                                          </div>
//                                      </div>

                  //console.log(jsonObj)
                  //alert(received_msg)
               };

               ws.onclose = function()
               {
                  // websocket is closed.
                 // alert("websocket closed!!")

               };

         }

         $(document).ready(function(){
            $("#showDashboard").click(function(){
                   $("#graphs").removeClass('hidden');
                   $("#recommendation").addClass('hidden');
            });
            $("#showRecommendation").click(function(){
             $("#recommendation").removeClass('hidden');
             $("#graphs").addClass('hidden');
            });

            $(".bb").click(function(){
            alert($(this).data('l_id'))
            });
         });