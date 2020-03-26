import PySimpleGUI as sg
import json

my_dict = {"rgb(0,0,0)":" onclick='is1();'",
           "rgb(200,200,200)": " onclick='is2();'",
           "rgb(200,100,50)": " onclick='is3();'",
           "rgb(100,120,140)": " onclick='is4();'"}

#String to send to java
my_params = []


sg.theme('Material1')

layout0 = [ [sg.Text('tableART',size=(50,1))],
            [sg.Text('_'  * 80)],
            [sg.Text('Choose an image', size=(35, 1))],      
            [sg.InputText(''), sg.FileBrowse(key="_path1_")],
            [sg.Text('Choose an image Area', size=(35, 1))],      
            [sg.InputText(''), sg.FileBrowse(key="_path2_")]
            ]

layout1 = [[sg.Text('Choose color and parameters')],
             [sg.Multiline( default_text ="rgb(0,0,0) : onclick=' '", size=(45,10), key='_multi_')],
           [sg.Text('Choose range of colors which are similar (Higher number, less colors variety/num cells)')],
           [sg.Slider(range=(0, 10), orientation='h', key='_precision_', size=(10,10), default_value=4)]
           ]

layout2 = [[sg.Text('Nearly done!')],
           [ sg.Text ('just press on Make your magic button!\nand then your folder tableART should be created!\nAnd its files prob. too!')],
           [sg.Button('Make Magic!!',key='_magic_')]
           ]

layout = [[sg.Column(layout0,key="_col1_"),
           sg.Column(layout1, visible=False, key="_col2_"),
           sg.Column(layout2, visible=False, key="_col3_")],
           [sg.Button('Go back', key='back'),sg.Button('Next', key='next'), sg.Exit()]
          ]

window = sg.Window('tableART', layout)

screen=1;
while True:
    
    event, values = window.Read()
    
    if event is None or event == 'Exit':
        break
    
    if event == 'next':
        if screen == 2:        
            if values["_path1_"] and values["_path2_"] != "":
            
                my_params.append(values["_path1_"])
                my_params.append(values["_path2_"])
                with open('app.json', 'w') as fp:
                    json.dump(values['_multi_'], fp)
                my_params.append(values['_precision_'])
                window['_col2_'].update(visible=False)
                screen=3
                window['_col3_'].update(visible=True)
            
            else :
                sg.Popup('Oops, file information is missing! :(')
        else:        
            window[f'_col{screen}_'].update(visible=False)
            screen = screen + 1 if screen < 2 else 2
            window[f'_col{screen}_'].update(visible=True)
        
    if event == 'back':
        window[f'_col{screen}_'].update(visible=False)
        screen = screen - 1 if screen > 1 else 1
        window[f'_col{screen}_'].update(visible=True)

    
    if event == '_magic_':
        '''
Okeey, esto esta casi ya, solo queda unir los scripts.. y dibujar los juegos.. ejeje, nos ha tomaod un mes esta mierda..
puto golapp hhehe

'''
        print('hiii')
    
window.close()
[print(a) for a in my_params]    
    
