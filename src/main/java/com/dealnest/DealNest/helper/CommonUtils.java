package com.dealnest.DealNest.helper;

import com.dealnest.DealNest.entity.Product;
import com.dealnest.DealNest.entity.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtils {

    @Autowired
    private JavaMailSender mailSender;

    public  Boolean sendEmail(String resetUrl, String recipientMail) throws UnsupportedEncodingException, MessagingException {

        // Implement your email sending logic here
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("contosphere@gmail.com", "DealNest");
        messageHelper.setTo(recipientMail);
        
        String content = "<p>Hello,</p> <p>You have requested to reset your password.</p>"+
                "<p>Please click on the following link to reset your password:</p> <p><a href=\"" + resetUrl + "\">Reset Password</a></p>"+
                "<p>Thank you for using DealNest.</p>";
        messageHelper.setSubject("Reset Your Password");
        messageHelper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    public Boolean sendMailForOrder(ProductOrder productOrder) throws UnsupportedEncodingException, MessagingException {
        // Implement your email sending logic here
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("contosphere@gmail.com", "DealNest");
        messageHelper.setTo(productOrder.getOrderAddress().getEmail());

        String content = "<p>Hello " + productOrder.getOrderAddress().getName() + ",</p>" +
                "<p>Thank you for your order from <strong>DealNest</strong>! We are excited to let you know that your order has been successfully placed.</p>" +
                "<p><strong>Order Details:</strong></p>" +
                "<p>Order Number: " + productOrder.getOrderId() + "<br>" +
                "Order Date: " + productOrder.getOrderDate() + "</p>" +
                "<p><strong> Status: " + productOrder.getStatus() + "</strong></p>" +

                "<p><strong>Shipping Address:</strong><br>" +
                "Name: " + productOrder.getOrderAddress().getName() + "<br>" +
                "Email: " + productOrder.getOrderAddress().getEmail() + "<br>" +
                "Mobile: " + productOrder.getOrderAddress().getMobileNumber() + "<br>" +
                "Address: " + productOrder.getOrderAddress().getAddress() + "<br>" +
                "City: " + productOrder.getOrderAddress().getCity() + "<br>" +
                "Pin Code: " + productOrder.getOrderAddress().getPinCode() + "<br>" +
                "State: " + productOrder.getOrderAddress().getState() + "<br>" +
                "Landmark: " + productOrder.getOrderAddress().getLandmark() + "</p>";

        content += "<p><strong>Order Summary:</strong></p>" +
                "<p>Product Name: " + productOrder.getProducts().getTitle() + "<br>" +
                "Price: " + productOrder.getProducts().getDiscountedPrice() + "<br>" +
                "Quantity: " + productOrder.getQuantity() + "<br>" +
                "Total: " + productOrder.getPrice() + "</p>";

        content += "</table>" +
                "<p><strong>Total Order Amount: </strong>" + productOrder.getPrice() + "</p>" +
                "<p>We will notify you once your order has shipped. In the meantime, you can track your order status by logging into your account.</p>" +
                "<p>If you have any questions or need assistance, feel free to contact our customer support team at <a href='mailto:support@dealnest.com'>support@dealnest.com</a>.</p>" +
                "<p>Thank you for choosing DealNest!</p>" +
                "<p>Best Regards,<br>DealNest Team</p>";

        messageHelper.setSubject("Order Placed Successfully");
        messageHelper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public Boolean sendMailForOrderStatusChange(ProductOrder productOrder) throws UnsupportedEncodingException, MessagingException {
        // Implement your email sending logic here
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("contosphere@gmail.com", "DealNest");
        messageHelper.setTo(productOrder.getOrderAddress().getEmail());

        String content = "<p>Hello " + productOrder.getOrderAddress().getName() + ",</p>" +
                "<p>Thank you for your order from <strong>DealNest</strong>! We are excited to let you know that your order status has been changed.</p>" +
                "<p><strong>Order Details:</strong></p>" +
                "<p>Order Number: " + productOrder.getOrderId() + "<br>" +
                "Order Date: " + productOrder.getOrderDate() + "</p>" +
                "<p><strong> Status: " + productOrder.getStatus() + "</strong></p>" +

                "<p><strong>Shipping Address:</strong><br>" +
                "Name: " + productOrder.getOrderAddress().getName() + "<br>" +
                "Email: " + productOrder.getOrderAddress().getEmail() + "<br>" +
                "Mobile: " + productOrder.getOrderAddress().getMobileNumber() + "<br>" +
                "Address: " + productOrder.getOrderAddress().getAddress() + "<br>" +
                "City: " + productOrder.getOrderAddress().getCity() + "<br>" +
                "Pin Code: " + productOrder.getOrderAddress().getPinCode() + "<br>" +
                "State: " + productOrder.getOrderAddress().getState() + "<br>" +
                "Landmark: " + productOrder.getOrderAddress().getLandmark() + "</p>";

        content += "<p><strong>Order Summary:</strong></p>" +
                "<p>Product Name: " + productOrder.getProducts().getTitle() + "<br>" +
                "Price: " + productOrder.getProducts().getDiscountedPrice() + "<br>" +
                "Quantity: " + productOrder.getQuantity() + "<br>" +
                "Total: " + productOrder.getPrice() + "</p>";

        content += "</table>" +
                "<p><strong>Total Order Amount: </strong>" + productOrder.getPrice() + "</p>" +
                "<p>We will notify you once your order has shipped. In the meantime, you can track your order status by logging into your account.</p>" +
                "<p>If you have any questions or need assistance, feel free to contact our customer support team at <a href='mailto:support@dealnest.com'>support@dealnest.com</a>.</p>" +
                "<p>Thank you for choosing DealNest!</p>" +
                "<p>Best Regards,<br>DealNest Team</p>";

        messageHelper.setSubject("Order Status has been changed");
        messageHelper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public Boolean sendMailForOrderCancelled(ProductOrder productOrder) throws UnsupportedEncodingException, MessagingException {
        // Implement your email sending logic here
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("contosphere@gmail.com", "DealNest");
        messageHelper.setTo(productOrder.getOrderAddress().getEmail());

        // Emojis and custom content for cancellation
        String content = "<p>Hello " + productOrder.getOrderAddress().getName() + ",</p>" +
                "<p>We regret to inform you that your order from <strong>DealNest</strong> has been <strong>CANCELLED</strong> 😞.</p>" +
                "<p><strong>Order Details:</strong></p>" +
                "<p>Order Number: " + productOrder.getOrderId() + "<br>" +
                "Order Date: " + productOrder.getOrderDate() + "</p>" +
                "<p><strong>Status: ORDER CANCELLED</strong> ❌</p>" +

                "<p><strong>Shipping Address:</strong><br>" +
                "Name: " + productOrder.getOrderAddress().getName() + "<br>" +
                "Email: " + productOrder.getOrderAddress().getEmail() + "<br>" +
                "Mobile: " + productOrder.getOrderAddress().getMobileNumber() + "<br>" +
                "Address: " + productOrder.getOrderAddress().getAddress() + "<br>" +
                "City: " + productOrder.getOrderAddress().getCity() + "<br>" +
                "Pin Code: " + productOrder.getOrderAddress().getPinCode() + "<br>" +
                "State: " + productOrder.getOrderAddress().getState() + "<br>" +
                "Landmark: " + productOrder.getOrderAddress().getLandmark() + "</p>";

        // Product summary (if any products are involved in the canceled order)
        content += "<p><strong>Order Summary:</strong></p>" +
                "<p>Product Name: " + productOrder.getProducts().getTitle() + "<br>" +
                "Price: " + productOrder.getProducts().getDiscountedPrice() + "<br>" +
                "Quantity: " + productOrder.getQuantity() + "<br>" +
                "Total: " + productOrder.getPrice() + "</p>";

        content += "<p><strong>Total Order Amount: </strong>" + productOrder.getPrice() + "</p>" +
                "<p>We are sorry for the inconvenience caused. If you believe this cancellation was a mistake or if you need further assistance, please reach out to us.</p>" +
                "<p>If you have any questions, feel free to contact our customer support team at <a href='mailto:support@dealnest.com'>support@dealnest.com</a>.</p>" +
                "<p>Thank you for your understanding, and we hope to serve you better in the future.</p>" +
                "<p>Best Regards,<br>DealNest Team</p>";

        // Set subject and send the email
        messageHelper.setSubject("Your Order has been Cancelled");
        messageHelper.setText(content, true);
        mailSender.send(message);
            return false;  // Failed to send the email
        }
}
