import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";

    public static void main(String[] args) {

        MailService spy = new Spy(Logger.getLogger(Class.class.getName()));
        MailService thief = new Thief(10);
        MailService inspector = new Inspector();
        MailService[] msArray = {spy, thief, inspector};
        MailMessage mail1 = new MailMessage("Romeo", "Juliet", "I love you!");
        MailMessage mail2 = new MailMessage("Austin Powers", "James Bond", "Big secret!");
        MailPackage mail3 = new MailPackage("Romeo", "Juliet", new Package("Flowers", 15));
        MailPackage mail4 = new MailPackage("Romeo", "Juliet", new Package("Flowers", 25));
        MailPackage mail5 = new MailPackage("Austin Powers", "James Bond", new Package("weapons", 5));

        UntrustworthyMailWorker umw = new UntrustworthyMailWorker(msArray);
        try {
            umw.processMail(mail1);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
        try {
            umw.processMail(mail2);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
        try {
            umw.processMail(mail3);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
        try {
            umw.processMail(mail4);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
        try {
            umw.processMail(mail5);
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }

        System.out.println("Thief have stolen $" + ((Thief) thief).getStolenValue() + "!");
    }


    /*
Интерфейс: сущность, которую можно отправить по почте.
У такой сущности можно получить от кого и кому направляется письмо.
*/
    public static interface Sendable {
        String getFrom();

        String getTo();
    }

    /*
    Абстрактный класс,который позволяет абстрагировать логику хранения
    источника и получателя письма в соответствующих полях класса.
    */
    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }

    /*
Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
*/
    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    /*
Посылка, содержимое которой можно получить с помощью метода `getContent`
*/
    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    /*
Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
*/
    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    /*
Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
*/
    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

     /*
Класс, в котором скрыта логика настоящей почты
*/

    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            return mail;
        }
    }


    public static class UntrustworthyMailWorker implements MailService {

        private RealMailService realMS = new RealMailService();
        private MailService[] mailS;

        public UntrustworthyMailWorker(MailService[] mailS) {
            this.mailS = mailS;
        }

        public Sendable processMail(Sendable mail) {
            for (int i = 0; i < mailS.length; i++) {
                mail = mailS[i].processMail(mail);
            }
            return realMS.processMail(mail);
        }

        public RealMailService getRealMailService() {
            return realMS;
        }
    }

    public static class Spy implements MailService {
        private final Logger logger;

        public Spy(final Logger logger) {
            this.logger = logger;
        }

        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage) {
                MailMessage mail2 = (MailMessage) mail;
                if (mail2.getFrom().equals(AUSTIN_POWERS) || mail2.getTo().equals(AUSTIN_POWERS)) {
                    this.logger.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"",
                            new Object[]{mail2.getFrom(), mail2.getTo(), mail2.getMessage()});
                } else {
                    logger.log(Level.INFO, "Usual correspondence: from {0} to {1}",
                            new Object[]{mail2.getFrom(), mail2.getTo()});
                }
            }
            return mail;
        }
    }

    public static class Thief implements MailService {
        private int minPrice;
        private int stolenValue;

        public Thief(int minPrice) {
            this.minPrice = minPrice;
            this.stolenValue = 0;
        }

        public int getStolenValue() {
            return stolenValue;
        }

        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                MailPackage mail2 = (MailPackage) mail;
                if (mail2.getContent().getPrice() >= this.minPrice) {
                    this.stolenValue += mail2.getContent().getPrice();
                    return new MailPackage(mail2.getFrom(), mail2.getTo(), new Package("stones instead of " + mail2.getContent().getContent(), 0));
                }
            }
            return mail;
        }
    }

    public static class Inspector implements MailService {
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                MailPackage mail2 = (MailPackage) mail;
                if (mail2.getContent().getContent().contains(WEAPONS) ||
                        mail2.getContent().getContent().contains(BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException();
                }
                if (mail2.getContent().getContent().contains("stones")) {
                    throw new StolenPackageException();
                }
                return mail2;
            }

            return mail;
        }
    }

    public static class IllegalPackageException extends RuntimeException {
    }

    public static class StolenPackageException extends RuntimeException {
    }
}


