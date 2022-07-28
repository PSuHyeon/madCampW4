# TikiTalk

TikiTalk은 텍스트를 통한 의사소통에 의존하는 기존 채팅 앱들의 한계를 돌파하여 **더욱 빠르고 효율적인 소통**을 제안하는 애플리케이션입니다.

TikiTalk은 세 가지의 소통 방식을 지원합니다.

1. Text-to-Text

      Text-to-Text 방식은 많은 사용자가 익숙하게 사용하는 소통 방식입니다. 

      TikiTalk은 사용자의 타이핑 과정을 실시간으로 상대에게 전달함으로써 **실시간 소통**의 성질을 가지고 있습니다.
      
      이를 통해 사용자는 더욱 빠르게 상대방과 소통할 수 있습니다. 
      
      실시간 소통의 성질에 걸맞게 사용자는 접속중인 상대와만 채팅을 시작할 수 있습니다. 만약 상대가 오프라인 상태라면 "노크" 기능을 통해 앱에 접속하도록 요청 할 수 있습니다.
      
      상대와 주고받은 메세지는 잠시 후 자동으로 삭제되고, 중요한 메세지의 경우 LongClick을 통해 저장할 수 있습니다.
      
      별도의 텍스트 입력창 없이 곧바로 상대에게 말풍선이 보이는 UI가 특징적입니다.
      
      또한, 간단하게 fontSize를 조절하여 사용자의 감정을 전달할 수 있습니다.
      

2. Voice-to-Text

      양손이 자유롭지 않을 때, 전달하고 싶은 말이 많을 때, 마음이 급할 때 등 Text를 이용한 의사소통으로 불편함을 느낄 떄, 사용자는 음성을 이용한 의사소통을 선택할 수 있습니다. 
      
      이떄 상대방은 사용자가 Voice mode로 전환한 것을 알 수 있으며, 본인 또한 편의에 따라 의사소통 방식을 선택할 수 있습니다.

      1) 음성을 그대로 전달

            첫 번째로는 상대방에게 사용자의 목소리를 그대로 전달 할 수 있습니다. 

      2) 음성을 텍스트로 변환하여 전달(STT)

            두 번째로는 내 목소리를 실시간으로 텍스트로 전환하여 상대방에게 전달해 주는 STT(Speech-to-text) 기능을 이용할 수 있습니다. 

            TikiTalk은 말이 끝나는 시점을 감지하여 말풍선을 보내고, 사용자가 채팅방을 나갈 때까지 Continuous 하게 사용자의 음성을 감지하여 편리한 사용자 경험을 제공합니다. 

3. Voice-to-Voice


마지막으로 
## Development Setting

- OS: Android (minSdk: 21, targetSdk: 32)
- Language: Java
- IDE: Android Studio
- Target Device: Pixel2

### Author

[KAIST 전산학부 이혜림](https://github.com/hermioneee2)

[KAIST 전산학부 김주연](https://github.com/editadiary)

[KAIST 전산학부 박수현](https://github.com/PSuHyeon)
