__author__ = "Daksh Patel"

def capitalizeAll(object):
    if type(object)==dict:
        keys=object.keys()
        for key in keys:
            if type(object[key])==str:
                if object[key].startswith('http'):
                    continue
                object[key]=object[key].title()
            elif type(object[key])==list:
                object[key]=capitalizeAll(object[key])
            else:
                pass
    elif type(object)==list:
        for i in range(len(object)):
            if type(object[i])==dict:
                object[i]=capitalizeAll(object[i])
            elif type(object[i])==str:
                if object[i].startswith('http'):
                    continue
                object[i]=object[i].title()
            else:
                pass
    else:
        pass
    return object

def encryptEmail(email):
    li = email.split('@')
    handle = li[0][:2] + '*' * (len(li[0]) - 2)
    domain = li[1].split('.')
    dom = domain[0][:2] + '*' * (len(domain[0]) - 2)
    ain = domain[1][:1] + '*' * (len(domain[1]) - 1)
    final_email = '{}@{}.{}'.format(handle, dom, ain)
    return final_email


if __name__=='__main__':
    obj=[
        {
        1:'ferfr sdfd',
        2:['wewe,aas', 'wewe,aas']
        },
        {
            1: 'ferfr sdfd',
            2: 'wewe,aas',
            3:3
        }

    ]
    print(capitalizeAll(obj))